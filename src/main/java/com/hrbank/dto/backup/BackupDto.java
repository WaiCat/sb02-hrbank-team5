package com.hrbank.dto.backup;

import com.hrbank.entity.Backup;
import com.hrbank.enums.BackupStatus;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record BackupDto(
    Long id,
    String worker,
    OffsetDateTime startedAt,
    OffsetDateTime endedAt,
    BackupStatus status,
    Long fileId
) {
  public static BackupDto toDto(Backup backup) {
    return new BackupDto(
        backup.getId(),
        backup.getWorker(),
        convertToKST(backup.getStartedAt()),
        convertToKST(backup.getEndedAt()),
        backup.getStatus(),
        backup.getFile().getId()
    );
  }

  private static OffsetDateTime convertToKST(OffsetDateTime utcTime) {
    if (utcTime == null) {
      return null;
    }
    return utcTime.withOffsetSameInstant(ZoneOffset.ofHours(9));
  }
}
