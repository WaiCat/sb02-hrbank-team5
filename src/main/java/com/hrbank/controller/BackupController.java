package com.hrbank.controller;

import com.hrbank.dto.backup.BackupDto;
import com.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.hrbank.enums.BackupStatus;
import com.hrbank.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backups")
public class BackupController {
  private final BackupService backupService;

  @GetMapping
  public ResponseEntity<CursorPageResponseBackupDto> getAllBackups(
      @RequestParam(required = false) String worker,
      @RequestParam(required = false) BackupStatus status,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startedAtFrom,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startedAtTo,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false, defaultValue = "10") Integer size,
      @RequestParam(required = false, defaultValue = "startedAt") String sortField,
      @RequestParam(required = false, defaultValue = "DESC") String sortDirection
  ){
    CursorPageResponseBackupDto cursorPageResponseBackupDto = backupService.searchBackups(worker, status, startedAtFrom,startedAtTo,idAfter,cursor,size,sortField,sortDirection);
    return ResponseEntity.ok(cursorPageResponseBackupDto);
  }

  @PostMapping
  public ResponseEntity<BackupDto> createBackup(HttpServletRequest servletRequest){
      BackupDto backupDto = backupService.runBackup(servletRequest.getRemoteAddr());
      return ResponseEntity.ok(backupDto);
  }

  @GetMapping(value = "/latest")
  public ResponseEntity<BackupDto> getLatestBackup(@RequestParam(required = false, defaultValue = "COMPLETED") BackupStatus status){
    BackupDto backupDto = backupService.findLatestBackupByStatus(status);
    return ResponseEntity.ok(backupDto);
  }
}
