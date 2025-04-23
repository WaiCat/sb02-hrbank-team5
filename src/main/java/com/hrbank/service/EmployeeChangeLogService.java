package com.hrbank.service;

import com.hrbank.dto.employeeChangeLog.EmployeeChangeLogSearchRequest;
import com.hrbank.entity.Employee;
import com.hrbank.entity.EmployeeChangeLog;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeChangeLogService {

  Page<EmployeeChangeLog> searchLogs(EmployeeChangeLogSearchRequest request, Pageable pageable);

  Optional<EmployeeChangeLog> findWithDetailsById(UUID id);

  void saveChangeLog(Employee employeeBefore, Employee employeeAfter, String memo, String ip);
}
