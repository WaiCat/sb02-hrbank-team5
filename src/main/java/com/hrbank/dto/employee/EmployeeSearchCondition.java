package com.hrbank.dto.employee;

import com.hrbank.enums.EmployeeStatus;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeSearchCondition {
  private String nameOrEmail;
  private String department;
  private String position;
  private String employeeNumber;
  private LocalDate hireDateFrom;
  private LocalDate hireDateTo;
  private EmployeeStatus status;
  private Long lastId; // 커서 기반 페이징용
  private String sortBy; // name, hireDate, employeeNumber 중 하나
}

