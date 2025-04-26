package com.hrbank.controller;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeCreateRequest;
import com.hrbank.dto.employee.EmployeeDistributionDto;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.dto.employee.EmployeeUpdateRequest;
import com.hrbank.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  @Operation(summary = "직원 목록 조회")
  @GetMapping
  public ResponseEntity<CursorPageResponseEmployeeDto> searchEmployees(@ModelAttribute EmployeeSearchCondition condition) {
    return ResponseEntity.ok(employeeService.searchEmployees(condition));
  }

  @Operation(summary = "직원 수정")
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

  @Operation(summary = "직원 등록")
  @PostMapping
  public ResponseEntity<EmployeeDto> createEmployee(
      @RequestPart("employee") @Valid EmployeeCreateRequest request,
      @RequestPart("profileImage") MultipartFile profileImage, HttpServletRequest httpRequest) {
    String ip = httpRequest.getRemoteAddr();
    return ResponseEntity.ok(employeeService.create(request, profileImage, ip));
  }

  @Operation(summary = "직원 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable Long id, HttpServletRequest httpRequest) {
    String ip = httpRequest.getRemoteAddr();
    employeeService.delete(id, ip);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "직원 상세 조회")
  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> getEmployeeDetails(@PathVariable Long id) {
    EmployeeDto employeeDto = employeeService.findById(id);
    return ResponseEntity.ok(employeeDto);
  }

  @Operation(summary = "직원 수 조회")
  @GetMapping("/count")
  public long getEmployeeCount(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate toDate) {

    return employeeService.getEmployeeCount(status, fromDate, toDate);
  }

  @Operation(summary = "직원 수 추이 조회")
  @GetMapping("/stats/trend")
  public Page<EmployeeTrendDto> getEmployeeTrends(EmployeeSearchCondition condition, Pageable pageable) {
    return employeeService.findEmployeeTrends(condition, pageable);
  }

  @GetMapping("/stats/distribution")
  @Operation(summary = "직원 분포 조회")
  public List<EmployeeDistributionDto> getEmployeeDistribution(
          @RequestParam(defaultValue = "department") String groupBy,
          @RequestParam(defaultValue = "ACTIVE") String status
  ) {
    return employeeService.getEmployeeDistribution(groupBy, status);
  }
}
