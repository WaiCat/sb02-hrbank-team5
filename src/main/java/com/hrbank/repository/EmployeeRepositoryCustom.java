package com.hrbank.repository;

import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.entity.Employee;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepositoryCustom {
  Page<Employee> findAllWithFilter(EmployeeSearchCondition condition, Pageable pageable);
  List<EmployeeTrendDto> findEmployeeTrends(LocalDate from, LocalDate to, String unit);
}