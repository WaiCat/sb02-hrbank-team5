package com.hrbank.service;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeCreateRequest;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeUpdateRequest;

public interface EmployeeService {
  CursorPageResponseEmployeeDto searchEmployees(EmployeeSearchCondition condition);
  
  EmployeeDto update(Long id, EmployeeUpdateRequest request, String ip);
  
  EmployeeDto create(EmployeeCreateRequest request);

  EmployeeDto delete(Long id);
}
