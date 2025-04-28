package com.hrbank.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
      @ApiResponse(responseCode = "404", description = "잘못된 요청",
          content = @Content(examples = @ExampleObject(
              value = "{\"code\": \"BINARY_CONTENT_NOT_FOUND\", "
                  + "\"status\": \"404\", "
                  + "\"message\": \"프로필 이미지를 찾을 수 없습니다.\", "
                  + "\"timestamp\": \"2025-03-06T05:39:06.152068Z\","
                  + "\"details\": \"/api/files/2/download\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류",
          content = @Content(examples = @ExampleObject(
              value = "{\"code\": \"FILE_READ_ERROR\", "
                  + "\"status\": \"500\", "
                  + "\"message\": \"파일 읽기 중 오류가 발생했습니다.\", "
                  + "\"timestamp\": \"2025-03-06T05:39:06.152068Z\","
                  + "\"details\": \"/api/files/2/download\"}")))
  })
  ResponseEntity<Resource> download(@Parameter(description = "파일 ID") Long id);
}
