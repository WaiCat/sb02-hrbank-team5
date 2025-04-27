package com.hrbank.dto.employee;


import java.time.LocalDate;
import lombok.Builder;

@Builder
public record EmployeeTrendDto (
    LocalDate date,
    Long count,
    Long change,
    Double changeRate
){
  public static EmployeeTrendDto of(LocalDate date, Long count, Long change, Double changeRate) {
    return EmployeeTrendDto.builder()
        .date(date)
        .count(count)
        .change(change)
        .changeRate(changeRate)
        .build();
  }
}
