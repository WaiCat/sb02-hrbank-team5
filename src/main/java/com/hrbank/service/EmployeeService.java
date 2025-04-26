package com.hrbank.service;

import com.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeCreateRequest;
import com.hrbank.dto.employee.EmployeeDistributionDto;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.dto.employee.EmployeeUpdateRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
  CursorPageResponseEmployeeDto searchEmployees(EmployeeSearchCondition condition);

  EmployeeDto update(Long id, EmployeeUpdateRequest request, BinaryContentCreateRequest fileRequest, String ip);

  EmployeeDto create(EmployeeCreateRequest request, MultipartFile profileImage, String ip);

  void delete(Long id, String ip);

  EmployeeDto findById(Long id);

  List<EmployeeTrendDto> findEmployeeTrends(LocalDate from, LocalDate to, String unit);

  long getEmployeeCount(String status, LocalDate fromDate, LocalDate toDate);

  List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, String status);
}
