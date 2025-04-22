package com.hrbank.dto.employee;

import java.util.List;

public record CursorPageResponseEmployeeDto(
    List<EmployeeDto> content,
    Long nextCursorId, //Employee.id 가 현재 Long 타입
    boolean hasNext
) {}