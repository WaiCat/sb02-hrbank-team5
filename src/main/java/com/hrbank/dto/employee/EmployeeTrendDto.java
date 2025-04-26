package com.hrbank.dto.employee;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeTrendDto {
  private String date;
  private Long count;
  private Long change;
  private Double changeRate;

  public EmployeeTrendDto(String date, Long count) {
    this.date = date;
    this.count = count;
    this.change = 0L;
    this.changeRate = 0.0;
  }
}
