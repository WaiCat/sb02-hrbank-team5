package com.hrbank.mapper;

import com.hrbank.dto.employeeChangeLog.ChangeLogDto;
import com.hrbank.dto.employeeChangeLog.DiffDto;
import com.hrbank.entity.EmployeeChangeLog;
import com.hrbank.entity.EmployeeChangeLogDetail;
import java.util.List;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Mapper(componentModel = "spring")
public interface EmployeeChangeLogMapper {
  ChangeLogDto toDto(EmployeeChangeLog entity);
  DiffDto toDiffDto(EmployeeChangeLogDetail detail);
  List<DiffDto> toDiffDtoList(List<EmployeeChangeLogDetail> details);
}
