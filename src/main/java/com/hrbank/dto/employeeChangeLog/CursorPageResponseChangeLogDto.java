package com.hrbank.dto.employeeChangeLog;

import java.util.List;

// 목록 조회 응답용 cursor dto
public record CursorPageResponseChangeLogDto(
    List<ChangeLogDto> content,       // logs → content
    String nextCursor,                // ID → Base64 등 필요한 포맷
    Long nextIdAfter,                 // optional
    int size,                         // 요청한 size
    long totalElements,              // 전체 개수
    boolean hasNext
) {}
