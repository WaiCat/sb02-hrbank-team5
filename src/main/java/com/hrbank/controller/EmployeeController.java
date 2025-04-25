package com.hrbank.controller;

import com.hrbank.dto.employee.*;
import com.hrbank.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

  @GetMapping("/stats/distribution")
  @Operation(summary = "직원 분포 조회")
  public List<EmployeeDistributionDto> getEmployeeDistribution(
          @RequestParam(defaultValue = "department") String groupBy,
          @RequestParam(defaultValue = "ACTIVE") String status
  ) {
    return employeeService.getEmployeeDistribution(groupBy, status);
  }
}
