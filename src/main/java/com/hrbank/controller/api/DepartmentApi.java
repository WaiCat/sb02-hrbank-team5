package com.hrbank.controller.api;

import com.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.hrbank.dto.department.DepartmentCreateRequest;
import com.hrbank.dto.department.DepartmentDto;
import com.hrbank.dto.department.DepartmentUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Department", description = "부서 관리 API")
public interface DepartmentApi {

    @Operation(summary = "부서 등록")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "부서가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = DepartmentDto.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "잘못된 요청 또는 중복된 이름",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 오류",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            )
    })
    ResponseEntity<DepartmentDto> createDepartment(
            @Parameter(
                    description = "부서 생성 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ) DepartmentCreateRequest request
    );

    @Operation(summary = "부서 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = DepartmentDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "부서를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 오류",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            )
    })
    ResponseEntity<DepartmentDto> getDepartment(
            @Parameter(description = "부서 ID") Long id
    );

    @Operation(summary = "부서 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = DepartmentDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "부서를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400", description = "잘못된 요청 또는 중복된 이름",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 오류",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            )
    })
    ResponseEntity<DepartmentDto> updateDepartment(
            @Parameter(description = "부서 ID") Long id,
            @Parameter(description = "수정할 부서 정보") DepartmentUpdateRequest request
    );

    @Operation(summary = "부서 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404", description = "부서를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 오류",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            )
    })
    ResponseEntity<Void> deleteDepartment(
            @Parameter(description = "삭제할 부서 ID") Long id
    );

    @Operation(summary = "부서 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CursorPageResponseDepartmentDto.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "잘못된 요청",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 오류",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n"
                                            + "  \"timestamp\": \"2025-03-06T05:39:06.152068Z\",\n"
                                            + "  \"status\": 400,\n"
                                            + "  \"message\": \"잘못된 요청입니다.\",\n"
                                            + "  \"details\": \"부서 코드는 필수입니다.\"\n"
                                            + "}"
                            )
                    )
            )
    })
    ResponseEntity<CursorPageResponseDepartmentDto> getDepartments(
            @Parameter(description = "부서 이름 또는 설명") String nameOrDescription,
            @Parameter(description = "이전 페이지 마지막 요소 ID") Long idAfter,
            @Parameter(description = "커서 (다음 페이지 시작점)") String cursor,
            @Parameter(description = "페이지 크기") Integer size,
            @Parameter(description = "정렬 필드 (name 또는 establishedDate)") String sortField,
            @Parameter(description = "정렬 방향 (asc 또는 desc)") String sortDirection
    );
}