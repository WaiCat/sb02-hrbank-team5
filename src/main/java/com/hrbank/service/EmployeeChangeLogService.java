package com.hrbank.service;

import com.hrbank.dto.employeeChangeLog.CursorPageResponseChangeLogDto;
import com.hrbank.dto.employeeChangeLog.DiffDto;
import com.hrbank.dto.employeeChangeLog.EmployeeChangeLogSearchRequest;
import com.hrbank.entity.Employee;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface EmployeeChangeLogService {

  // 변경점 로그 저장
  void saveChangeLog(Employee employeeBefore, Employee employeeAfter, String memo, String ip);

  //  특성 시점 이후 변경된 내용이 있는지 확인
  boolean hasChangeSince(LocalDateTime at);

  // 이력 목록 조회
  CursorPageResponseChangeLogDto search(EmployeeChangeLogSearchRequest request, Long idAfter, String cursor, int size);

  List<DiffDto> getChangeLogDetails(Long changeLogId);

  long countChangeLogs(OffsetDateTime fromDate, OffsetDateTime toDate);
}
