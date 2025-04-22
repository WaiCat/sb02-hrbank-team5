package com.hrbank.service.basic;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.repository.EmployeeRepositoryCustom;
import com.hrbank.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicEmployeeService implements EmployeeService {

  private final EmployeeRepositoryCustom employeeRepository;

  @Override
  public CursorPageResponseEmployeeDto searchEmployees(EmployeeSearchCondition condition) {
    return employeeRepository.findAllWithFilter(condition);
  }
}