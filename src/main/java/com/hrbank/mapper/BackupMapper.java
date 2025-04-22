package com.hrbank.mapper;

import com.hrbank.dto.backup.BackupDto;
import com.hrbank.entity.Backup;

public class BackupMapper {
  public static BackupDto toDto(Backup entity) {
    if (entity == null) return null;

    return new BackupDto(
        entity.getId(),
        entity.getWorker(),
        entity.getStartedAt(),
        entity.getEndedAt(),
        entity.getStatus(),
        entity.getFile() != null ? entity.getFile().getId() : null
    );
  }

}
