package com.hrbank.service;

import com.hrbank.dto.employeeChangeLog.EmployeeChangeLogSearchRequest;
import com.hrbank.entity.Employee;
import com.hrbank.entity.EmployeeChangeLog;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeChangeLogService {

  Page<EmployeeChangeLog> searchLogs(EmployeeChangeLogSearchRequest request, Pageable pageable);

  Optional<EmployeeChangeLog> findWithDetailsById(UUID id);

  // 변경점 로그 저장
  void saveChangeLog(Employee employeeBefore, Employee employeeAfter, String memo, String ip);

  //  특성 시점 이후 변경된 내용이 있는지 확인
  boolean hasChangeSince(LocalDateTime at);
}
