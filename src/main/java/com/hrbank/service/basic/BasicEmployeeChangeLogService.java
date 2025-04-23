package com.hrbank.service.basic;

import com.hrbank.dto.employeeChangeLog.ChangeLogDto;
import com.hrbank.dto.employeeChangeLog.CursorPageResponseChangeLogDto;
import com.hrbank.dto.employeeChangeLog.DiffDto;
import com.hrbank.dto.employeeChangeLog.EmployeeChangeLogSearchRequest;
import com.hrbank.entity.BinaryContent;
import com.hrbank.entity.Department;
import com.hrbank.entity.Employee;
import com.hrbank.entity.EmployeeChangeLog;
import com.hrbank.entity.EmployeeChangeLogDetail;
import com.hrbank.enums.EmployeeChangeLogType;
import com.hrbank.exception.ErrorCode;
import com.hrbank.exception.RestException;
import com.hrbank.mapper.EmployeeChangeLogMapper;
import com.hrbank.repository.EmployeeChangeLogRepository;
import com.hrbank.repository.specification.EmployeeChangeLogSpecification;
import com.hrbank.service.EmployeeChangeLogService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicEmployeeChangeLogService implements EmployeeChangeLogService {

  private final EmployeeChangeLogRepository changeLogRepository;
  private final EmployeeChangeLogMapper changeLogMapper;

  // 직원 정보가 생성, 수정, 삭제 될 때 호출되어야 함
  // 변경점에 대한 로그를 생성하는 메서드
  @Override
  public void saveChangeLog(Employee before, Employee after, String memo, String ipAddress) {

    EmployeeChangeLogType type;
    String employeeNumber;

    // 1. 변경 유형 판별
    if (before == null && after == null) {
      throw new RestException(ErrorCode.INVALID_CHANGE_LOG_DATA);
    } else if (before == null && after != null) {
      type = EmployeeChangeLogType.CREATED;
      employeeNumber = after.getEmployeeNumber();
    } else if (before != null && after == null) {
      type = EmployeeChangeLogType.DELETED;
      employeeNumber = before.getEmployeeNumber();
    } else {
      type = EmployeeChangeLogType.UPDATED;
      employeeNumber = after.getEmployeeNumber();
    }

    // 2. 로그 객체 생성
    EmployeeChangeLog changeLog = new EmployeeChangeLog(
        type,
        employeeNumber,
        memo,
        ipAddress,
        LocalDateTime.now()
    );

    // 3. UPDATED일 경우에만 필드 비교 후 detail 생성
    if (type == EmployeeChangeLogType.UPDATED && before != null && after != null) {

      addDetailIfChanged(changeLog, "name", before.getName(), after.getName());
      addDetailIfChanged(changeLog, "email", before.getEmail(), after.getEmail());
      addDetailIfChanged(changeLog, "position", before.getPosition(), after.getPosition());
      addDetailIfChanged(changeLog, "status", before.getStatus().name(), after.getStatus().name());

      // 부서는 null 가능성 있음
      addDetailIfChanged(changeLog, "department",
          Optional.ofNullable(before.getDepartment()).map(Department::getName).orElse(null),
          Optional.ofNullable(after.getDepartment()).map(Department::getName).orElse(null));

      // 프로필 이미지는 null 가능성 있음
      addDetailIfChanged(changeLog, "profileImage",
          Optional.ofNullable(before.getProfileImage()).map(BinaryContent::getFileName).orElse(null),
          Optional.ofNullable(after.getProfileImage()).map(BinaryContent::getFileName).orElse(null));
    }

    // 4. 저장
    changeLogRepository.save(changeLog);
  }

  @Override
  public Page<EmployeeChangeLog> searchLogs(EmployeeChangeLogSearchRequest request, Pageable pageable) {
    Specification<EmployeeChangeLog> spec = Specification.<EmployeeChangeLog>where(null)
        .and(EmployeeChangeLogSpecification.employeeNumberContains(request.employeeNumber()))
        .and(EmployeeChangeLogSpecification.memoContains(request.memo()))
        .and(EmployeeChangeLogSpecification.ipAddressContains(request.ipAddress()))
        .and(EmployeeChangeLogSpecification.typeEquals(request.type()))
        .and(EmployeeChangeLogSpecification.atBetween(request.atFrom(), request.atTo()));

    return changeLogRepository.findAll(spec, pageable);
  }

  @Override
  public Optional<EmployeeChangeLog> findWithDetailsById(Long id) {
    return changeLogRepository.findById(id); // 추후 fetch join 필요하면 custom query로 변경
  }

  @Override
  public boolean hasChangeSince(LocalDateTime at) {
    return changeLogRepository.existsByAtAfter(at);
  }

  @Override
  public CursorPageResponseChangeLogDto search(EmployeeChangeLogSearchRequest request, Long idAfter, String cursor, int size) {
    Long resolvedIdAfter = idAfter;
    // cursor가 있으면 우선적으로 사용
    if (cursor != null) {
      try {
        resolvedIdAfter = Long.parseLong(cursor);
      } catch (NumberFormatException e) {
        throw new RestException(ErrorCode.INVALID_CURSOR);
      }
    }

    // 기본 정렬 필드 및 방향 설정
    String sortField = Optional.ofNullable(request.sortField()).orElse("at");
    Sort.Direction direction = "asc".equalsIgnoreCase(request.sortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;

    // 정렬 조건 생성 (id 추가로 안정성 확보)
    Sort sort = Sort.by(direction, sortField).and(Sort.by(direction, "id"));
    Pageable pageable = PageRequest.of(0, size, sort);

    // 검색 조건 Specification 생성
    Specification<EmployeeChangeLog> spec = EmployeeChangeLogSpecification.search(request);

    // 커서 기반 페이징 조건 추가
    if (resolvedIdAfter != null) {
      Long finalIdAfter = resolvedIdAfter;
      spec = spec.and((root, query, cb) -> cb.lessThan(root.get("id").as(Long.class), finalIdAfter));
    }


    // 검색 실행
    Page<EmployeeChangeLog> page = changeLogRepository.findAll(spec, pageable);

    // DTO 매핑
    List<ChangeLogDto> logs = page.getContent().stream()
        .map(changeLogMapper::toDto)
        .toList();

    // 다음 커서 계산
    Long nextIdAfter = page.hasNext() ? logs.get(logs.size() - 1).id() : null;
    String nextCursor = nextIdAfter != null ? String.valueOf(nextIdAfter) : null;

    return new CursorPageResponseChangeLogDto(
        logs,                                // content
        nextCursor,                        // nextCursor
        nextIdAfter,                          // nextIdAfter
        size,                                // size 요청값 그대로
        page.getTotalElements(),             // 전체 개수
        page.hasNext()                       // hasNext
    );
  }

  @Override
  public List<DiffDto> getChangeLogDetails(Long changeLogId) {
    EmployeeChangeLog log = changeLogRepository.findById(changeLogId)
        .orElseThrow(() -> new RestException(ErrorCode.CHANGE_LOG_NOT_FOUND));

    return changeLogMapper.toDiffDtoList(log.getDetails());
  }


  // 변경된 로그를 저장하는 메서드. 반복되어 별도 분리
  private void addDetailIfChanged(EmployeeChangeLog log, String field, String before, String after) {
    if (!Objects.equals(before, after)) {
      log.addDetail(new EmployeeChangeLogDetail(log, field, before, after));
    }
  }
}

