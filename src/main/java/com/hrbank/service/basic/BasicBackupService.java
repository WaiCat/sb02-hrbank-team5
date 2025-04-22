package com.hrbank.service.basic;

import com.hrbank.dto.backup.BackupDto;
import com.hrbank.entity.Backup;
import com.hrbank.enums.BackupStatus;
import com.hrbank.mapper.BackupMapper;
import com.hrbank.repository.BackupRepository;
import com.hrbank.service.BackupService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicBackupService implements BackupService {

  private BackupRepository backupRepository;
  private BackupMapper backupMapper;
  private EmployeeRepository employeeRepository;
  private BinaryContentRepository binaryContentRepository;

  @Override
  public void runBackup(String requesterIp) {
    if (!isBackupRequired()) {
      // 백업 필요 없으면 SKIPPED 처리
      Backup skipped = Backup.builder()
          .worker(requesterIp)
          .status(BackupStatus.SKIPPED)
          .startedAt(Instant.now())
          .endedAt(Instant.now())
          .build();
      backupRepository.save(skipped);
      return;
    }

    // 백업 이력 생성
    BackupDto inProgress = createInProgressBackup(requesterIp);

    try {
      // 백업 파일 생성
      // 파일 생성 추후 구현
      Long fileId = generateBackupFile(inProgress.id());

      // 성공 처리
      markBackupCompleted(inProgress.id(), fileId);
    } catch (Exception e) {
      // 에러 로그 파일 저장 (추후 구현)
      Long logFileId = saveErrorLogFile(e); // 가정: 오류 로그 저장 메서드

      // 실패 처리
      markBackupFailed(inProgress.id(), logFileId);
    }
  }

  @Override
  public boolean isBackupRequired() {
    Optional<Backup> lastCompletedBackup = backupRepository.findTopByStatusOrderByEndedAtDesc(
        BackupStatus.COMPLETED);
    if(lastCompletedBackup.isEmpty()){
      return true;
    }

    Instant lastBackupTime = lastCompletedBackup.get().getEndedAt();

    // 특정 시간 이후 직원 업데이트 내역 확인
    // 추후 구현 필요
    return employeeRepository.existsByUpdatedAtAfter(lastBackupTime);
  }

  @Override
  public BackupDto createInProgressBackup(String requesterIp) {
    Backup backup = Backup.builder()
        .worker(requesterIp)
        .status(BackupStatus.IN_PROGRESS)
        .startedAt(Instant.now())
        .build();

    Backup saved = backupRepository.save(backup);
    return backupMapper.toDto(saved);
  }

  @Override
  public void markBackupCompleted(Long backupId, Long fileId) {
    Backup backup = backupRepository.findById(backupId)
        .orElseThrow(() -> new EntityNotFoundException("백업을 찾을 수 없습니다."));

    BinaryContent file = binaryContentRepository.findById(fileId)
        .orElseThrow(() -> new EntityNotFoundException("파일을 찾을 수 없습니다."));

    backup.setStatus(BackupStatus.COMPLETED);
    backup.setEndedAt(Instant.now());
    backup.setFile(file);
  }

  @Override
  public void markBackupFailed(Long backupId, Long logFileId) {
    Backup backup = backupRepository.findById(backupId)
        .orElseThrow(() -> new EntityNotFoundException("백업을 찾을 수 없습니다."));

    BinaryContent logFile = binaryContentRepository.findById(logFileId)
        .orElseThrow(() -> new EntityNotFoundException("로그 파일을 찾을 수 없습니다."));

    backup.setStatus(BackupStatus.FAILED);
    backup.setEndedAt(Instant.now());
    backup.setFile(logFile);
  }
}
