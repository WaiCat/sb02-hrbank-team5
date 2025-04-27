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
  private String departmentName; // 프론트 요청을 위해서 존재
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
  private String sortDirection = "asc";

  private LocalDate from;   // 요청 파라미터: from
  private LocalDate to;     // 요청 파라미터: to
  private String unit = "month"; // 요청 파라미터: unit (기본값 month)

  // departmentName 이 들어오면 department 에도 세팅
  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
    this.department = departmentName;
  }
}