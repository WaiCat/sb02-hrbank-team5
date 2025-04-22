package com.hrbank.dto.department;

import java.util.List;

public record CursorPageResponseDepartmentDto(
        List<DepartmentDto> content,
        Long lastId,
        boolean hasNext
) {}
