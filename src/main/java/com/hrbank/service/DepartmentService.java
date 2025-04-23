package com.hrbank.service;

import com.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.hrbank.dto.department.DepartmentCreateRequest;
import com.hrbank.dto.department.DepartmentDto;
import com.hrbank.dto.department.DepartmentUpdateRequest;

public interface DepartmentService {
    DepartmentDto createDepartment(DepartmentCreateRequest request);
    DepartmentDto getDepartmentById(Long id);
    DepartmentDto updateDepartment(Long id, DepartmentUpdateRequest request);
    void deleteDepartment(Long id);

    CursorPageResponseDepartmentDto getDepartments(
            String nameOrDescription,
            Long idAfter,
            String cursor,
            Integer size,
            String sortField,
            String sortDirection);
}
