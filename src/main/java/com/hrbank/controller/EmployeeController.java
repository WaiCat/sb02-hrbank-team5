package com.hrbank.controller;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeCreateRequest;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.dto.employee.EmployeeUpdateRequest;
import com.hrbank.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  @GetMapping
  public ResponseEntity<CursorPageResponseEmployeeDto> searchEmployees(@ModelAttribute EmployeeSearchCondition condition) {
    return ResponseEntity.ok(employeeService.searchEmployees(condition));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<EmployeeDto> updateEmployee(
      @PathVariable Long id,
      @RequestBody EmployeeUpdateRequest request,
      HttpServletRequest httpRequest
  ) {
    String ip = httpRequest.getRemoteAddr();
    EmployeeDto updated = employeeService.update(id, request, ip);
    return ResponseEntity.ok(updated);
  }

  @PostMapping
  public ResponseEntity<EmployeeDto> createEmployee(
      @RequestPart("employee") @Valid EmployeeCreateRequest request,
      @RequestPart("profileImage") MultipartFile profileImage, HttpServletRequest httpRequest) {
    String ip = httpRequest.getRemoteAddr();
    return ResponseEntity.ok(employeeService.create(request, profileImage, ip));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable Long id, HttpServletRequest httpRequest) {
    String ip = httpRequest.getRemoteAddr();
    employeeService.delete(id, ip);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> getEmployeeDetails(@PathVariable Long id) {
    EmployeeDto employeeDto = employeeService.findById(id);
    return ResponseEntity.ok(employeeDto);
  }

  @GetMapping("/stats/trend")
  public Page<EmployeeTrendDto> getEmployeeTrends(EmployeeSearchCondition condition, Pageable pageable) {
    return employeeService.findEmployeeTrends(condition, pageable);
  }
}
