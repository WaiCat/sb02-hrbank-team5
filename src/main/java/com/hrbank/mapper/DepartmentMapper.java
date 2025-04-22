package com.hrbank.mapper;

import com.hrbank.dto.department.DepartmentDto;
import com.hrbank.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {
    public DepartmentDto toDto(Department department) {
        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getEstablishedDate()
        );
    }
}
