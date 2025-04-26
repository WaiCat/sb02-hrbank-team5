package com.hrbank.dto.employee;

import java.time.LocalDate;

public record EmployeeCreateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    LocalDate hireDate,
    String memo
    // 사번과 상태는 서버에서 자동 생성 / 초기화할 예정이니 입력받지 않음
) {}
