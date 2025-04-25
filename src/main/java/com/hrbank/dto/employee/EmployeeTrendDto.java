package com.hrbank.dto.employee;

import lombok.Data;

@Data
public class EmployeeTrendDto {
  private String data;
  private Long count;
  private Long change;
  private Double changeRate;
}
