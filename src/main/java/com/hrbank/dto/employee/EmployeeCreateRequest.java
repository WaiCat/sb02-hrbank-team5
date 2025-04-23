package com.hrbank.dto.employee;

import com.hrbank.enums.EmployeeStatus;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

public record EmployeeCreateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    LocalDate hireDate,
    MultipartFile profileImage,
    String memo
    // 사번과 상태는 서버에서 자동 생성 / 초기화할 예정이니 입력받지 않음
) {}
