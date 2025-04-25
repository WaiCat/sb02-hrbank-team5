package com.hrbank.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeTrendDto {
  private String data;
  private Long count;
  private Long change;
  private Double changeRate;
}