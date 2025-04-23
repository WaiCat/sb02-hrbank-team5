package com.hrbank.controller;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeCreateRequest;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeUpdateRequest;
import com.hrbank.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  @GetMapping
  public ResponseEntity<CursorPageResponseEmployeeDto> searchEmployees(@ModelAttribute EmployeeSearchCondition condition) {
    return ResponseEntity.ok(employeeService.searchEmployees(condition));
  }

  @PutMapping("/{id}")
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
  public ResponseEntity<EmployeeDto> createEmployee(@RequestBody @Valid EmployeeCreateRequest request) {
    return ResponseEntity.ok(employeeService.create(request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<EmployeeDto> deleteEmployee(@PathVariable Long id) {
    EmployeeDto deletedEmployee = employeeService.delete(id);
    return ResponseEntity.ok(deletedEmployee);
  }
}
