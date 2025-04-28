package com.hrbank.mapper;

import com.hrbank.dto.employeeChangeLog.ChangeLogDto;
import com.hrbank.dto.employeeChangeLog.DiffDto;
import com.hrbank.entity.EmployeeChangeLog;
import com.hrbank.entity.EmployeeChangeLogDetail;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EmployeeChangeLogMapper {
  ChangeLogDto toDto(EmployeeChangeLog entity);

  @Mapping(target = "before", source = "before", qualifiedByName = "translateStatus")
  @Mapping(target = "after", source = "after", qualifiedByName = "translateStatus")
  DiffDto toDiffDto(EmployeeChangeLogDetail detail);
  List<DiffDto> toDiffDtoList(List<EmployeeChangeLogDetail> details);

  @Named("translateStatus")
  default String translateStatus(String status) {
    if (status == null) return null;
    return switch (status) {
      case "ACTIVE" -> "재직중";
      case "ON_LEAVE" -> "휴직중";
      case "RESIGNED" -> "퇴사";
      default -> status;
    };
  }
}