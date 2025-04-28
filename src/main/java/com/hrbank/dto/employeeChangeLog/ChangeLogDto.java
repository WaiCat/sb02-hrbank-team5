package com.hrbank.dto.employeeChangeLog;

import com.hrbank.enums.EmployeeChangeLogType;
import java.time.OffsetDateTime;
import java.util.List;

// 상세 조회용 dto
public record ChangeLogDto(
    Long id,
    EmployeeChangeLogType type,
    String employeeNumber,
    String memo,
    String ipAddress,
    OffsetDateTime at,
    List<DiffDto> details
) {}