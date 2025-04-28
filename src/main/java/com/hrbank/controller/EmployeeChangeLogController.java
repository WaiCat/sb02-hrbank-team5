package com.hrbank.controller;

import com.hrbank.controller.api.EmployeeChangeLogControllerApi;
import com.hrbank.dto.employeeChangeLog.CursorPageResponseChangeLogDto;
import com.hrbank.dto.employeeChangeLog.DiffDto;
import com.hrbank.dto.employeeChangeLog.EmployeeChangeLogSearchRequest;
import com.hrbank.service.EmployeeChangeLogService;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class EmployeeChangeLogController implements EmployeeChangeLogControllerApi {

  private final EmployeeChangeLogService changeLogService;

  @GetMapping
  public ResponseEntity<CursorPageResponseChangeLogDto> getChangeLogs(
      @ModelAttribute EmployeeChangeLogSearchRequest request,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") int size
  ) {
    CursorPageResponseChangeLogDto result = changeLogService.search(request, idAfter, cursor, size);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}/diffs")
  public ResponseEntity<List<DiffDto>> getChangeLogDiffs(@PathVariable Long id) {
    List<DiffDto> details = changeLogService.getChangeLogDetails(id);
    return ResponseEntity.ok(details);
  }

  @GetMapping("/count")
  public ResponseEntity<Long> countChangeLogs(
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime toDate
  ) {
    long count = changeLogService.countChangeLogs(fromDate, toDate);
    return ResponseEntity.ok(count);
  }


}
