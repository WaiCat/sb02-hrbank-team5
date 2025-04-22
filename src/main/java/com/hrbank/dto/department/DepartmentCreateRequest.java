package com.hrbank.dto.department;

import java.time.LocalDate;

public record DepartmentCreateRequest(
        String name,
        String description,
        LocalDate establishedDate
) {}
