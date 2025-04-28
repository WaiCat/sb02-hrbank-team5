package com.hrbank.controller.api;

import com.hrbank.dto.employeeChangeLog.CursorPageResponseChangeLogDto;
import com.hrbank.dto.employeeChangeLog.DiffDto;
import com.hrbank.dto.employeeChangeLog.EmployeeChangeLogSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "직원 정보 수정 이력 관리", description = "직원 변경 이력 관리 API")
public interface EmployeeChangeLogControllerApi {

  @Operation(summary = "직원 변경 이력 목록 조회", description = "직원 변경 이력 목록을 검색합니다. 상세 변경 내용은 포함되지 않습니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CursorPageResponseChangeLogDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 정렬 필드", content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"400\", \"message\": \"잘못된 요청입니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"500\", \"message\": \"서버 오류가 발생했습니다.\"}")))
  })
  ResponseEntity<CursorPageResponseChangeLogDto> getChangeLogs(
      @Parameter(description = "검색 조건", content = @Content(schema = @Schema(implementation = EmployeeChangeLogSearchRequest.class)))
      @ModelAttribute EmployeeChangeLogSearchRequest request,

      @Parameter(description = "이전 페이지 마지막 ID")
      @RequestParam(required = false) Long idAfter,

      @Parameter(description = "커서 (이전 페이지 마지막 요소 ID)")
      @RequestParam(required = false) String cursor,

      @Parameter(description = "페이지 크기")
      @RequestParam(defaultValue = "10") int size
  );

  @Operation(summary = "변경 이력 상세 조회", description = "특정 변경 이력 ID에 대한 상세 변경 내용을 조회합니다. 변경 상세 내용이 포함됩니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = DiffDto.class))),
      @ApiResponse(responseCode = "404", description = "이력을 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"404\", \"message\": \"이력을 찾을 수 없습니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"500\", \"message\": \"서버 오류가 발생했습니다.\"}")))
  })
  ResponseEntity<List<DiffDto>> getChangeLogDiffs(
      @Parameter(description = "이력 ID") Long id
  );

  @Operation(summary = "변경 이력 건수 조회", description = "직원 정보 수정 이력 건수를 조회합니다. 파라미터를 제공하지 않으면 최근 일주일 데이터를 반환합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Long.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 날짜 범위", content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"400\", \"message\": \"잘못된 날짜입니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"500\", \"message\": \"서버 오류가 발생했습니다.\"}")))
  })
  ResponseEntity<Long> countChangeLogs(
      @Parameter(description = "시작 일시(기본값: 7일 전)")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,

      @Parameter(description = "종료 일시(기본값: 현재)")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate
  );
}
