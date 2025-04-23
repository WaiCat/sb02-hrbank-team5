package com.hrbank.dto.employeeChangeLog;

import com.hrbank.enums.EmployeeChangeLogType;
import java.time.LocalDateTime;

public record EmployeeChangeLogSearchRequest(
    String employeeNumber,
    EmployeeChangeLogType type,
    String memo,
    String ipAddress,
    LocalDateTime atFrom,
    LocalDateTime atTo,
    String sortField,
    String sortDirection
) {}
