package com.hrbank.mapper;

import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.entity.Employee;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
  @Mapping(source = "department.name", target = "departmentName")
  @Mapping(target = "profileImageId", expression = "java(employee.getProfileImageId())")
  EmployeeDto toDto(Employee employee);
  List<EmployeeDto> toDtoList(List<Employee> employees);
}
