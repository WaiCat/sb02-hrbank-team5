package com.hrbank.dto.employee;

import com.hrbank.enums.EmployeeStatus;
import java.time.LocalDate;
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

  private int page = 0; // 기본 페이지 번호
  private int size = 10; // 기본 페이지 크기
  private String sortField = "name"; // 기본 정렬 필드

  private String groupByUnit = "month"; // Trend 단위 그룹화
}