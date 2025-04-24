package com.hrbank.repository;

import com.hrbank.dto.employee.EmployeeSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.hrbank.entity.Employee;

public interface EmployeeRepositoryCustom {
  Page<Employee> findAllWithFilter(EmployeeSearchCondition condition, Pageable pageable);
}