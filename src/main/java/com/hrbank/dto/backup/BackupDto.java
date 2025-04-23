package com.hrbank.dto.backup;

import com.hrbank.enums.BackupStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record BackupDto(
    Long id,
    String worker,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    BackupStatus status,
    Long fileId
) {

}
