package com.hrbank.mapper;

import com.hrbank.dto.department.DepartmentDto;
import com.hrbank.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    // N+1 문제 해결을 위해 직접 employeeCount를 주입받도록 수정
    @Mapping(target = "employeeCount", source = "employeeCount")
    DepartmentDto toDto(Department department, int employeeCount);

}
