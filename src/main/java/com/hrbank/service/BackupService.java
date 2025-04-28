package com.hrbank.service;

import com.hrbank.dto.backup.BackupDto;
import com.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.hrbank.enums.BackupStatus;
import java.time.OffsetDateTime;

public interface BackupService {

  // 조건에 맞는 백업 목록 OffsetDateTime
  CursorPageResponseBackupDto searchBackups(String worker, BackupStatus status, OffsetDateTime from, OffsetDateTime to, Long idAfter, String cursor, Integer size, String sortField, String sortDirection);

  // status한 가장 최근 백업 조회
  BackupDto findLatestBackupByStatus(BackupStatus status);

  // 백업 수행
  BackupDto runBackup(String requesterIp);
}
