package com.hrbank.repository;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;

public interface EmployeeRepositoryCustom {
  CursorPageResponseEmployeeDto findAllWithFilter(EmployeeSearchCondition condition);
}
