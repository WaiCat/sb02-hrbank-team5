package com.hrbank.dto.employeeChangeLog;

import com.hrbank.enums.EmployeeChangeLogType;
import java.time.OffsetDateTime;

public record EmployeeChangeLogSearchRequest(
    String employeeNumber,
    EmployeeChangeLogType type,
    String memo,
    String ipAddress,
    OffsetDateTime atFrom,
    OffsetDateTime atTo,
    String sortField,
    String sortDirection
) {}
