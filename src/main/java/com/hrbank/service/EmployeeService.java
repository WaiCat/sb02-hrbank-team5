package com.hrbank.service;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeCreateRequest;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.dto.employee.EmployeeUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
  CursorPageResponseEmployeeDto searchEmployees(EmployeeSearchCondition condition);

  EmployeeDto update(Long id, EmployeeUpdateRequest request, String ip);

  EmployeeDto create(EmployeeCreateRequest request, MultipartFile profileImage, String ip);

  void delete(Long id, String ip);

  EmployeeDto findById(Long id);

  Page<EmployeeTrendDto> findEmployeeTrends(EmployeeSearchCondition condition, Pageable pageable);
}
