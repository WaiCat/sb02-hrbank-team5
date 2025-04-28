package com.hrbank.dto.error;

import com.hrbank.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 응답")
public record ErrorResponse(
    @Schema(description = "에러 코드")
    String code,
    @Schema(description = "HTTP 상태 코드")
    int status,
    @Schema(description = "에러 메세지")
    String message,
    @Schema(description = "에러 발생 시각")
    String timestamp,
    @Schema(description = "상세 설명")
    String details
) {
  public static ErrorResponse of(ErrorCode errorCode, String details, String timestamp) {
    return new ErrorResponse(
        errorCode.name(),
        errorCode.getStatus().value(),
        errorCode.getMessage(),
        timestamp,
        details
    );
  }
}