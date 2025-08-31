package com.edukit.studentrecord.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.studentrecord.controller.request.StudentRecordUpdateRequest;
import com.edukit.studentrecord.facade.response.StudentRecordDetailResponse;
import com.edukit.studentrecord.facade.response.StudentRecordsGetResponse;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "생활기록부 관리", description = "생활기록부 관리 API")
public interface StudentRecordApi {
    @Operation(
            summary = "생활기록부 목록 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "생활기록부 목록 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "message": "요청이 성공했습니다.",
                                              "data": {
                                                "studentRecords": [
                                                  {
                                                    "recordId": 1,
                                                    "grade": 2,
                                                    "classNumber": 3,
                                                    "studentNumber": 15,
                                                    "studentName": "홍길동",
                                                    "description": "수학 수업에 적극적으로 참여하는 모습을 보인다."
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(
                                            name = "유효하지 않은 생활기록부 항목",
                                            description = "존재하지 않는 생활기록부 항목을 요청한 경우",
                                            value = """
                                                      {
                                                        "code": "SR-40403",
                                                        "message": "유효하지 않은 생활기록부 항목입니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<StudentRecordsGetResponse>> getStudentRecords(
            @MemberId final long memberId,
            @PathVariable final StudentRecordType recordType,
            @RequestParam(required = false) final Integer grade,
            @RequestParam(required = false) final Integer classNumber,
            @RequestParam(required = false) final String search,
            @RequestParam(required = false) final Long lastRecordId
    );

    @Operation(
            summary = "생활기록부 수정"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "생활기록부 수정 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "message": "요청이 성공했습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "생활기록부 내용이 없는 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "description": "생활기록부 내용은 필수입니다."
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
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> updateStudentRecord(
            @MemberId final long memberId,
            @PathVariable final long recordId,
            @RequestBody @Valid final StudentRecordUpdateRequest request
    );

    @Operation(
            summary = "생활기록부 상세 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "생활기록부 상세 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "message": "요청이 성공했습니다.",
                                              "data": {
                                                "description": "수학 수업에 적극적으로 참여하는 모습을 보인다."
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(
                                            name = "학생 기록 미존재",
                                            description = "조회하려는 학생 기록이 존재하지 않는 경우",
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
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<StudentRecordDetailResponse>> getStudentRecordDetail(
            @MemberId final long memberId,
            @PathVariable final long recordId
    );

    @Operation(
            summary = "생활기록부 목록 엑셀 다운로드"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(
                                            name = "유효하지 않은 생활기록부 항목",
                                            description = "존재하지 않는 생활기록부 항목을 요청한 경우",
                                            value = """
                                                      {
                                                        "code": "SR-40403",
                                                        "message": "유효하지 않은 생활기록부 항목입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "엑셀 파일 생성 실패",
                                            description = "엑셀 파일 생성 중 오류가 발생한 경우",
                                            value = """
                                                      {
                                                        "code": "ST-50004",
                                                        "message": "엑셀 파일 생성 중 오류가 발생했습니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<byte[]> downloadStudentRecordExcel(
            @MemberId final long memberId,
            @PathVariable final StudentRecordType recordType);
}
