package com.hrbank.controller;

import com.hrbank.controller.api.DepartmentApi;
import com.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.hrbank.dto.department.DepartmentCreateRequest;
import com.hrbank.dto.department.DepartmentDto;
import com.hrbank.dto.department.DepartmentUpdateRequest;
import com.hrbank.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/departments")
public class DepartmentController implements DepartmentApi {
    private final DepartmentService departmentService;

    @PostMapping
    @Override
    public ResponseEntity<DepartmentDto> createDepartment(
            @RequestBody DepartmentCreateRequest request
    ) {
        DepartmentDto createdDepartment = departmentService.createDepartment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDepartment);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<DepartmentDto> getDepartment(
            @PathVariable("id") Long id
    ) {
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(department);
    }

    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<DepartmentDto> updateDepartment(
            @PathVariable("id") Long id,
            @RequestBody DepartmentUpdateRequest request
    ) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteDepartment(@PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    @Override
    public ResponseEntity<CursorPageResponseDepartmentDto> getDepartments(
            @RequestParam(required = false) String nameOrDescription,
            @RequestParam(required = false) Long idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "establishedDate") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection
    ) {
        CursorPageResponseDepartmentDto response = departmentService.getDepartments(
                nameOrDescription, idAfter, cursor, size, sortField, sortDirection);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}