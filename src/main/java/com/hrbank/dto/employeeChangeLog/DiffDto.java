package com.hrbank.dto.employeeChangeLog;

// 목록 조회용 dto
public record DiffDto(
    String propertyName,
    String before,
    String after
) {}