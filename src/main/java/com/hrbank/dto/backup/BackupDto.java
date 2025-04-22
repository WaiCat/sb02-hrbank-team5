package com.hrbank.dto.backup;

import com.hrbank.enums.BackupStatus;
import java.time.Instant;
import java.util.UUID;

public record BackupDto(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    BackupStatus status,
    UUID fileId
) {

}
