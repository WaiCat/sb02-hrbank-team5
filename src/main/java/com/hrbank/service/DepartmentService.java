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

    // 부서 목록 조회 (이름 정렬)
    CursorPageResponseDepartmentDto getDepartmentsByNameSorting(
            String nameKeyword,
            String descriptionKeyword,
            Long cursorId,
            int limit);
    // 부서 목록 조회 (설립일 정렬)

    CursorPageResponseDepartmentDto getDepartmentsByEstablishedDateSorting(
            String nameKeyword,
            String descriptionKeyword,
            Long cursorId,
            int limit);
}
