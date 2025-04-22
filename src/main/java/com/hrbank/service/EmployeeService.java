package com.hrbank.service;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;

public interface EmployeeService {
  CursorPageResponseEmployeeDto searchEmployees(EmployeeSearchCondition condition);
}
