package com.hrbank.dto.employeeChangeLog;

import com.hrbank.enums.EmployeeChangeLogType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record EmployeeChangeLogSearchRequest(
    @Schema(description = "대상 직원 사번", example = "EMP001")
    String employeeNumber,

    @Schema(description = "이력 유형", example = "UPDATED")
    EmployeeChangeLogType type,

    @Schema(description = "내용", example = "주소 변경")
    String memo,

    @Schema(description = "IP 주소", example = "192.168.1.1")
    String ipAddress,

    @Schema(description = "수정 일시(부터)", example = "2025-04-20T00:00:00Z")
    OffsetDateTime atFrom,

    @Schema(description = "수정 일시(까지)", example = "2025-04-27T23:59:59Z")
    OffsetDateTime atTo,

    @Schema(description = "정렬 필드 (ipAddress, at)", example = "at")
    String sortField,

    @Schema(description = "정렬 방향 (asc, desc)", example = "desc")
    String sortDirection
) {}
