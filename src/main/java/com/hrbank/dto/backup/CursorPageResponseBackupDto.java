package com.hrbank.dto.backup;

import java.util.List;

public record CursorPageResponseBackupDto(
    List<BackupDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

}
