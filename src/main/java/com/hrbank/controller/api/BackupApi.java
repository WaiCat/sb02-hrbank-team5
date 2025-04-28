package com.hrbank.controller.api;

import com.hrbank.dto.backup.BackupDto;
import com.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.hrbank.dto.error.ErrorResponse;
import com.hrbank.enums.BackupStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

public interface BackupApi {

  @Operation(summary = "백업 실행")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "백업 정상 실행"),
      @ApiResponse(responseCode = "409", description = "이미 진행 중인 백업이 있음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  ResponseEntity<BackupDto> createBackup(HttpServletRequest servletRequest);

  @Operation(summary = "백업 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "정상 조회"),
      @ApiResponse(responseCode = "404", description = "백업을 찾을 수 없습니다.",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  ResponseEntity<CursorPageResponseBackupDto> getAllBackups(
      @Parameter(description = "백업 작업자 IP 또는 이름")
      String worker,

      @Parameter(description = "백업 상태")
      BackupStatus status,

      @Parameter(description = "백업 시작 시간 이후", example = "2025-04-20T00:00:00")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      OffsetDateTime startedAtFrom,

      @Parameter(description = "백업 시작 시간 이전", example = "2025-04-22T23:59:59")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      OffsetDateTime startedAtTo,

      @Parameter(description = "마지막으로 조회된 ID 이후의 데이터")
      Long idAfter,

      @Parameter(description = "다음 페이지 조회를 위한 커서")
      String cursor,

      @Parameter(
          description = "페이지 크기",
          example = "10",
          schema = @Schema(defaultValue = "10")
      )
      Integer size,

      @Parameter(
          description = "정렬 필드 (startedAt, endedAt)",
          example = "startedAt",
          schema = @Schema(defaultValue = "startedAt")
      )
      String sortField,

      @Parameter(
          description = "정렬 방향 (ASC, DESC)",
          example = "DESC",
          schema = @Schema(defaultValue = "DESC")
      )
      String sortDirection
  );

  @Operation(summary = "최신 백업 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "정상 조회"),
      @ApiResponse(responseCode = "404", description = "요청한 상태에 해당하는 백업이 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  ResponseEntity<BackupDto> getLatestBackup(
      @Parameter(
          description = "조회할 백업 상태",
          schema = @Schema(defaultValue = "COMPLETED")
      )
      BackupStatus status
  );
}
