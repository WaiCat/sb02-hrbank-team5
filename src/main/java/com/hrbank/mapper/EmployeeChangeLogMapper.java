package com.hrbank.mapper;

import com.hrbank.dto.employeeChangeLog.ChangeLogDto;
import com.hrbank.dto.employeeChangeLog.DiffDto;
import com.hrbank.entity.EmployeeChangeLog;
import com.hrbank.entity.EmployeeChangeLogDetail;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EmployeeChangeLogMapper {

  public ChangeLogDto toDto(EmployeeChangeLog entity) {
    List<DiffDto> details = entity.getDetails()
        .stream()
        .map(this::toDiffDto)
        .collect(Collectors.toList());

    return new ChangeLogDto(
        entity.getId(),
        entity.getType(),
        entity.getEmployeeNumber(),
        entity.getMemo(),
        entity.getIpAddress(),
        entity.getAt()
    );
  }

  public DiffDto toDiffDto(EmployeeChangeLogDetail detail) {
    return new DiffDto(
        detail.getPropertyName(),
        detail.getBefore(),
        detail.getAfter()
    );
  }
}
