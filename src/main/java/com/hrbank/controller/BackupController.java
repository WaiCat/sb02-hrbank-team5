package com.hrbank.controller;

import com.hrbank.controller.api.BackupApi;
import com.hrbank.dto.backup.BackupDto;
import com.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.hrbank.enums.BackupStatus;
import com.hrbank.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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
public class BackupController implements BackupApi {
  private final BackupService backupService;

  @Override
  @GetMapping
  public ResponseEntity<CursorPageResponseBackupDto> getAllBackups(
      @RequestParam(required = false) String worker,
      @RequestParam(required = false) BackupStatus status,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAtFrom,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAtTo,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "startedAt") String sortField,
      @RequestParam(defaultValue = "DESC") String sortDirection
  ){
    CursorPageResponseBackupDto cursorPageResponseBackupDto = backupService.searchBackups(worker, status, startedAtFrom, startedAtTo, idAfter, cursor, size, sortField, sortDirection);
    return ResponseEntity.ok(cursorPageResponseBackupDto);
  }

  @Override
  @PostMapping
  public ResponseEntity<BackupDto> createBackup(HttpServletRequest servletRequest){
      BackupDto backupDto = backupService.runBackup(servletRequest.getRemoteAddr());
      return ResponseEntity.ok(backupDto);
  }

  @Override
  @GetMapping(value = "/latest")
  public ResponseEntity<BackupDto> getLatestBackup(@RequestParam( defaultValue = "COMPLETED") BackupStatus status){
    BackupDto backupDto = backupService.findLatestBackupByStatus(status);
    return ResponseEntity.ok(backupDto);
  }
}
