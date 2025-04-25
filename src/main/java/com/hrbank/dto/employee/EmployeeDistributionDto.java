package com.hrbank.dto.employee;

public record EmployeeDistributionDto(
        String groupKey,
        Long count,
        double percentage
) {}
