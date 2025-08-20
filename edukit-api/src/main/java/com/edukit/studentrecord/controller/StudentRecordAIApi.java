package com.edukit.studentrecord.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.studentrecord.controller.request.StudentRecordPromptRequest;
import com.edukit.studentrecord.facade.response.StudentRecordTaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "생활기록부 AI 생성", description = "생활기록부 AI 생성 API")
public interface StudentRecordAIApi {

    @Operation(
            summary = "AI 생활기록부 생성 작업 시작",
            description = "AI를 통해 생활기록부를 생성하기 위한 작업을 생성하고 작업 ID를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "작업 생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EdukitResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "message": "요청이 성공했습니다.",
                                              "data": {
                                                "taskId": 123
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락 - 바이트 수",
                                            description = "바이트 수가 양수가 아닌 경우",
                                            value = """
                                                    {
                                                      "code": "FAIL-400",
                                                      "message": "validation 오류",
                                                      "data": {
                                                        "byteCount": "바이트 수는 양수여야 합니다."
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "필수 값 누락 - 프롬프트",
                                            description = "프롬프트가 비어있는 경우",
                                            value = """
                                                    {
                                                      "code": "FAIL-400",
                                                      "message": "validation 오류",
                                                      "data": {
                                                        "prompt": "필수 입력값입니다."
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "학생 기록 미존재",
                                            description = "수정하려는 학생 기록이 존재하지 않는 경우",
                                            value = """
                                                    {
                                                      "code": "SR-40401",
                                                      "message": "해당 학생 기록이 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "권한 없음",
                                            description = "해당 학생 기록에 대한 권한이 없는 경우",
                                            value = """
                                                    {
                                                      "code": "SR-40302",
                                                      "message": "해당 학생 기록에 대한 권한이 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Redis 메시지 처리 오류",
                                            description = "Redis 메시지 처리 중 오류가 발생한 경우",
                                            value = """
                                                    {
                                                      "code": "R-50001",
                                                      "message": "Redis 메시지 처리 중 오류가 발생했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "AI 작업 완료 실패",
                                            description = "AI 작업 완료 중 오류가 발생한 경우",
                                            value = """
                                                    {
                                                      "code": "SR-50007",
                                                      "message": "AI 작업 완료에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<StudentRecordTaskResponse>> aiGenerateStudentRecord(
            @Parameter(hidden = true) @MemberId final long memberId,
            @Parameter(
                    name = "recordId",
                    description = "생활기록부 ID",
                    required = true,
                    example = "1"
            ) @PathVariable final long recordId,
            @Parameter(
                    name = "request",
                    description = "AI 생성 요청 정보",
                    required = true
            ) @RequestBody @Valid final StudentRecordPromptRequest request
    );

    @Operation(
            summary = "AI 생활기록부 생성 결과 스트리밍",
            description = "AI 생활기록부 생성 작업의 결과를 실시간으로 스트리밍합니다. " +
                         "응답이 3번 전송되면 자동으로 작업이 완료되고 연결이 종료됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "스트리밍 연결 성공",
                    content = @Content(
                            mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
                            schema = @Schema(
                                    type = "string",
                                    description = "Server-Sent Events 스트림",
                                    example = """
                                            event: ai-response
                                            data: {"taskId":123,"content":"생성된 생활기록부 내용 1","isComplete":false}
                                            
                                            event: ai-response
                                            data: {"taskId":123,"content":"생성된 생활기록부 내용 2","isComplete":false}
                                            
                                            event: ai-response
                                            data: {"taskId":123,"content":"생성된 생활기록부 내용 3","isComplete":true}
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "작업 미존재",
                                            description = "존재하지 않는 작업 ID인 경우",
                                            value = """
                                                    {
                                                      "code": "SR-40401",
                                                      "message": "해당 학생 기록이 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "SSE 타임아웃",
                                            description = "SSE 연결이 타임아웃된 경우",
                                            value = "SSE Timeout"
                                    ),
                                    @ExampleObject(
                                            name = "Redis 메시지 처리 오류",
                                            description = "Redis 메시지 처리 중 오류가 발생한 경우",
                                            value = """
                                                    {
                                                      "code": "R-50001",
                                                      "message": "Redis 메시지 처리 중 오류가 발생했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "AI 작업 완료 실패",
                                            description = "AI 작업 완료 중 오류가 발생한 경우",
                                            value = """
                                                    {
                                                      "code": "SR-50007",
                                                      "message": "AI 작업 완료에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    SseEmitter streamStudentRecordResponse(
            @Parameter(hidden = true) @MemberId final long memberId,
            @Parameter(
                    name = "taskId",
                    description = "AI 생성 작업 ID",
                    required = true,
                    example = "123"
            ) @PathVariable final long taskId
    );
}
