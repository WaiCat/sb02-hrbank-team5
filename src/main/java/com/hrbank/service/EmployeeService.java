package com.hrbank.service;

import com.hrbank.dto.employee.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
  CursorPageResponseEmployeeDto searchEmployees(EmployeeSearchCondition condition);

  EmployeeDto update(Long id, EmployeeUpdateRequest request, String ip);

  EmployeeDto create(EmployeeCreateRequest request, MultipartFile profileImage, String ip);

  void delete(Long id, String ip);

  EmployeeDto findById(Long id);

  List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, String status);
}
