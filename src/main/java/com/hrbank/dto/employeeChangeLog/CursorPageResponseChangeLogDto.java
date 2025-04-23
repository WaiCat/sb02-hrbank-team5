package com.hrbank.dto.employeeChangeLog;

import java.util.List;

// 목록 조회 응답용 cursor dto
public record CursorPageResponseChangeLogDto(
    List<ChangeLogDto> logs,
    boolean hasNext,
    Long nextCursor
) {}
