package com.hrbank.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

@Tag(name = "파일 관리", description = "파일 관리 API")
public interface BinaryContentApi {
  // 파일 다운로드 API
  @Operation(summary = "파일 다운로드")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "다운로드 성공",
          content = @Content(mediaType = "*/*", schema = @Schema(type = "string", format = "binary"))),
      @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음",
          content = @Content(mediaType = "*/*")),
      @ApiResponse(responseCode = "500", description = "서버오류",
          content = @Content(mediaType = "*/*"))
  })
  ResponseEntity<Resource> download(@Parameter(description = "파일 ID") Long id);
}
