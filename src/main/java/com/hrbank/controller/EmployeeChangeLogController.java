package com.hrbank.controller;

import com.hrbank.service.EmployeeChangeLogService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class EmployeeChangeLogController {

  private final EmployeeChangeLogService changeLogService;

  @GetMapping("/exists")
  public ResponseEntity<Boolean> hasChangedSince(
      @RequestParam("since") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime since) {
    // http://localhost:8080/api/change-logs/exists?since=2025-04-22T00:00:00
    // 특정 시점 이후 변경된 내용이 있으면 true, 없으면 false를 반환합니다.
    return ResponseEntity.ok(changeLogService.hasChangeSince(since));
  }

}
