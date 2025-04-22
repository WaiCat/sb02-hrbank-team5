package com.hrbank.dto.employeeChangeLog;

import com.hrbank.enums.EmployeeChangeLogType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeChangeLogSearchRequest {
  private String employeeNumber;
  private EmployeeChangeLogType type;
  private String memo;
  private String ipAddress;
  private LocalDateTime atFrom;
  private LocalDateTime atTo;
}

