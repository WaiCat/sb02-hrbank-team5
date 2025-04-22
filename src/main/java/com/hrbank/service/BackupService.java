package com.hrbank.service;

import com.hrbank.dto.backup.BackupDto;

public interface BackupService {

  //백업 수행
  void runBackup(String requesterIp);

  // 백업 필요 여부 판단: 가장 최근 완료 이후 변경 있으면 true
  boolean isBackupRequired();

  // 백업 이력 생성
  BackupDto createInProgressBackup(String requesterIp);

  // 백업 성공 시 상태/종료시간/파일ID 갱신
  void markBackupCompleted(Long backupId, Long fileId);

  // 백업 실패 시 상태/종료시간/로그파일ID 갱신
  void markBackupFailed(Long backupId, Long logFileId);
}
