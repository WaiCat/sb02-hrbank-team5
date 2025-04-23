package com.hrbank.mapper;

import com.hrbank.dto.employeeChangeLog.ChangeLogDto;
import com.hrbank.dto.employeeChangeLog.DiffDto;
import com.hrbank.entity.EmployeeChangeLog;
import com.hrbank.entity.EmployeeChangeLogDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeChangeLogMapper {
  ChangeLogDto toDto(EmployeeChangeLog entity);
  DiffDto toDiffDto(EmployeeChangeLogDetail detail);
}
