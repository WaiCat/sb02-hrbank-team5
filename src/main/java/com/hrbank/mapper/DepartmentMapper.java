package com.hrbank.mapper;

import com.hrbank.dto.department.DepartmentDto;
import com.hrbank.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    @Mapping(target = "employeeCount", expression = "java(department.getEmployees() != null ? department.getEmployees().size() : 0)")
    DepartmentDto toDto(Department department);
    List<DepartmentDto> toDtoList(List<Department> departments);
}
