package com.hrbank.mapper;

import com.hrbank.dto.department.DepartmentDto;
import com.hrbank.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    // 직원 수를 직접 주입받아 DTO로 변환
    @Mapping(target = "employeeCount", source = "employeeCount")
    DepartmentDto toDto(Department department, int employeeCount);

}
