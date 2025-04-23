package com.hrbank.scheduler;

import com.hrbank.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackupScheduler {
    private final BackupService backupService;

  @Scheduled(cron = "0 0 * * * *") // 매 시간 정각
  public void runHourlyBackup() {
    log.info("Hourly backup triggered by scheduler");
    backupService.runBackup("system");  // 작업자는 시스템
  }
}
