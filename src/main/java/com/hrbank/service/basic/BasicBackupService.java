package com.hrbank.service.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrbank.dto.backup.BackupDto;
import com.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.hrbank.entity.Backup;
import com.hrbank.entity.BinaryContent;
import com.hrbank.enums.BackupStatus;
import com.hrbank.mapper.BackupMapper;
import com.hrbank.repository.BackupRepository;
import com.hrbank.repository.BinaryContentRepository;
import com.hrbank.repository.EmployeeChangeLogRepository;
import com.hrbank.repository.EmployeeRepository;
import com.hrbank.service.BackupService;
import jakarta.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BasicBackupService implements BackupService {

  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;
  private final EmployeeRepository employeeRepository;
  private final EmployeeChangeLogRepository employeeChangeLogRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public CursorPageResponseBackupDto searchBackups(
      String worker, BackupStatus status, Instant from, Instant to,
      Long id, String cursor, Integer size, String sortField, String sortDirection) {

    // 필터링
    List<Backup> filtered = backupRepository.findAll().stream()
        .filter(backup -> worker == null || backup.getWorker().equals(worker))
        .filter(backup -> status == null || backup.getStatus() == status)
        .filter(backup -> from == null || backup.getStartedAt().isAfter(from))
        .filter(backup -> to == null || backup.getStartedAt().isBefore(to))
        .toList();

    // 정렬 기준 설정
    Comparator<Backup> comparator = "endedAt".equalsIgnoreCase(sortField)
        ? Comparator.comparing(Backup::getEndedAt)
        : Comparator.comparing(Backup::getStartedAt);

    if ("DESC".equalsIgnoreCase(sortDirection)) {
      comparator = comparator.reversed();
    }

    filtered.sort(comparator);

    // 커서 디코딩 및 시작 인덱스 계산
    int startIndex = 0;
    Long cursorId = null;

    if (cursor != null && !cursor.isBlank()) {
      cursorId = decodeCursor(cursor);

      for (int i = 0; i < filtered.size(); i++) {
        if (Objects.equals(filtered.get(i).getId(), cursorId)) {
          startIndex = i + 1;
          break;
        }
      }
    }

    // 페이지 처리
    int endIndex = Math.min(startIndex + size, filtered.size());
    List<BackupDto> page = filtered.subList(startIndex, endIndex).stream()
        .map(backupMapper::toDto)
        .toList();

    boolean hasNext = endIndex < filtered.size();
    Long nextIdAfter = hasNext ? filtered.get(endIndex - 1).getId() : null;
    String nextCursor = hasNext ? encodeCursor(nextIdAfter) : null;

    return new CursorPageResponseBackupDto(
        page,
        nextCursor,
        nextIdAfter,
        size,
        filtered.size(),
        hasNext
    );
  }

  @Override
  public BackupDto findLatestBackupByStatus(BackupStatus status) {
    return backupRepository.findTopByStatusOrderByEndedAtDesc(status)
        .map(backupMapper::toDto)
        .orElseThrow(() ->
            new IllegalArgumentException("요청한 상태에 해당하는 백업이 존재하지 않습니다.")
        );
  }


  @Override
  @Transactional
  public BackupDto runBackup(String requesterIp) {
    if (!isBackupRequired()) {
      // 백업 필요 없으면 SKIPPED 처리
      Backup skipped = Backup.builder()
          .worker(requesterIp)
          .status(BackupStatus.SKIPPED)
          .startedAt(Instant.now())
          .endedAt(Instant.now())
          .build();
      backupRepository.save(skipped);
      return backupMapper.toDto(skipped);
    }

    // 백업 이력 생성
    BackupDto inProgress = createInProgressBackup(requesterIp);

    try {
      // 백업 파일 생성
      Long fileId = generateBackupFile();

      // 성공 처리
      markBackupCompleted(inProgress.id(), fileId);


    } catch (Exception e) {
      // 로그 파일 생성
      Long logFileId = saveErrorLogFile(e);

      // 실패 처리
      markBackupFailed(inProgress.id(), logFileId);
    }

    return inProgress;
  }

  private boolean isBackupRequired() {
    Optional<Backup> lastCompletedBackup = backupRepository.findTopByStatusOrderByEndedAtDesc(
        BackupStatus.COMPLETED);
    if(lastCompletedBackup.isEmpty()){
      return true;
    }

    Instant lastBackupTime = lastCompletedBackup.get().getEndedAt();

    // 특정 시간 이후 직원 업데이트 내역 확인
    // 추후 구현 필요
    return employeeChangeLogRepository.existsByUpdatedAtAfter(lastBackupTime);
  }

  private BackupDto createInProgressBackup(String requesterIp) {
    Backup backup = Backup.builder()
        .worker(requesterIp)
        .status(BackupStatus.IN_PROGRESS)
        .startedAt(Instant.now())
        .build();

    Backup saved = backupRepository.save(backup);
    return backupMapper.toDto(saved);
  }

  private void markBackupCompleted(Long backupId, Long fileId) {
    Backup backup = backupRepository.findById(backupId)
        .orElseThrow(() -> new EntityNotFoundException("백업을 찾을 수 없습니다."));

    BinaryContent file = binaryContentRepository.findById(fileId)
        .orElseThrow(() -> new EntityNotFoundException("파일을 찾을 수 없습니다."));

    backup.completeBackup(file);
  }

  private void markBackupFailed(Long backupId, Long logFileId) {
    Backup backup = backupRepository.findById(backupId)
        .orElseThrow(() -> new EntityNotFoundException("백업을 찾을 수 없습니다."));

    BinaryContent logFile = binaryContentRepository.findById(logFileId)
        .orElseThrow(() -> new EntityNotFoundException("로그 파일을 찾을 수 없습니다."));

    backup.failBackup(logFile);
  }

  private Long decodeCursor(String cursor) {
    try {
      byte[] decodedBytes = Base64.getDecoder().decode(cursor);
      String json = new String(decodedBytes, StandardCharsets.UTF_8);

      // JSON에서 "id" 필드 추출
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode node = objectMapper.readTree(json);
      return node.has("id") ? node.get("id").asLong() : null;
    } catch (Exception e) {
      log.warn("Invalid cursor: {}", cursor);
      throw new IllegalArgumentException("Invalid cursor format: " + cursor, e);
    }
  }

  private String encodeCursor(Long id) {
    if (id == null) return null;
    String json = "{\"id\":" + id + "}";
    return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
  }
}


