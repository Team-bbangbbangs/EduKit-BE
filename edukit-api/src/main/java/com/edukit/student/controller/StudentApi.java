package com.edukit.student.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.student.controller.request.StudentCreateRequest;
import com.edukit.student.controller.request.StudentDeleteRequest;
import com.edukit.student.controller.request.StudentUpdateRequest;
import com.edukit.student.facade.response.StudentUploadResponse;
import com.edukit.student.facade.response.StudentsGetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "학생 관리", description = "학생 관리 API")
public interface StudentApi {

    @Operation(summary = "Excel 파일로 학생 일괄 업로드")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학생 업로드 성공",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "message": "요청이 성공했습니다.",
                                              "data": {
                                                "successCount": 15,
                                                "failureCount": 3,
                                                "invalidRows": []
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
                                            name = "잘못된 파일 형식",
                                            description = "Excel 파일이 아닌 파일을 업로드한 경우",
                                            value = """
                                                      {
                                                        "code": "ST-40003",
                                                        "message": "엑셀 파일 형식이 올바르지 않습니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "파일 읽기 오류",
                                            description = "Excel 파일이 손상되었거나 읽을 수 없는 경우",
                                            value = """
                                                      {
                                                        "code": "ST-40002",
                                                        "message": "Excel 파일을 읽을 수 없습니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<EdukitResponse<StudentUploadResponse>> uploadStudentExcel(
            @MemberId long memberId,
            @RequestParam("file") final MultipartFile file
    );

    @Operation(summary = "학생 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학생 생성 성공",
                    content = @Content(
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
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "학년, 반, 번호, 이름 중 하나라도 누락된 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "grade": "학년은 필수 입력 항목입니다.",
                                                          "classNumber": "반은 필수 입력 항목입니다.",
                                                          "studentNumber": "번호는 필수 입력 항목입니다.",
                                                          "studentName": "이름은 필수 입력 항목입니다."
                                                        }
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 등록된 학생",
                                            description = "동일한 학년, 반, 번호의 학생이 이미 존재하는 경우",
                                            value = """
                                                      {
                                                        "code": "ST-40905",
                                                        "message": "이미 등록된 학생입니다."
                                                      }
                                                    """
                                    ),
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
    ResponseEntity<EdukitResponse<Void>> createStudent(
            @MemberId final long memberId,
            @RequestBody @Valid final StudentCreateRequest request
    );

    @Operation(summary = "학생 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학생 수정 성공",
                    content = @Content(
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
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "학년, 반, 번호, 이름 중 하나라도 누락된 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "grade": "학년은 필수 입력 항목입니다.",
                                                          "classNumber": "반은 필수 입력 항목입니다.",
                                                          "studentNumber": "번호는 필수 입력 항목입니다.",
                                                          "studentName": "이름은 필수 입력 항목입니다."
                                                        }
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "학생 미존재",
                                            description = "수정하려는 학생이 존재하지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "ST-40401",
                                                        "message": "해당 학생이 존재하지 않습니다."
                                                      }
                                                    """
                                    ),
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
    ResponseEntity<EdukitResponse<Void>> updateStudent(
            @MemberId final long memberId,
            @PathVariable final long studentId,
            @RequestBody @Valid final StudentUpdateRequest request
    );

    @Operation(summary = "학생 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학생 삭제 성공",
                    content = @Content(
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
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "삭제할 학생 ID 목록이 비어있는 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "studentIds": "삭제할 학생 id는 하나 이상 입력해야 합니다."
                                                        }
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "학생 미존재",
                                            description = "삭제하려는 학생 중 존재하지 않는 학생이 있는 경우",
                                            value = """
                                                      {
                                                        "code": "ST-40401",
                                                        "message": "해당 학생이 존재하지 않습니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> deleteStudents(
            @MemberId final long memberId,
            @RequestBody @Valid final StudentDeleteRequest request
    );

    @Operation(summary = "학생 목록 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학생 목록 조회 성공",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "message": "요청이 성공했습니다.",
                                              "data": {
                                                "studentCount": 10,
                                                "grades": [1, 2, 3],
                                                "classNumbers": [1, 2, 3],
                                                "students": [
                                                  {
                                                    "studentId": 1,
                                                    "grade": 1,
                                                    "classNumber": 1,
                                                    "studentNumber": 1,
                                                    "studentName": "홍길동",
                                                    "recordTypes": ["세부능력 및 특기사항", "행동특성 및 종합의견"]
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
                                            name = "페이지 크기 최솟값 위반",
                                            description = "페이지 크기가 1보다 작은 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "pageSize": "1 이상이어야 합니다"
                                                        }
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "페이지 크기 최댓값 위반",
                                            description = "페이지 크기가 100보다 큰 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "pageSize": "100 이하여야 합니다"
                                                        }
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<StudentsGetResponse>> getStudents(
            @MemberId final long memberId,
            @Parameter(description = "학년 목록")
            @RequestParam(required = false) final List<Integer> grades,
            @Parameter(description = "반 목록")
            @RequestParam(required = false) final List<Integer> classNumbers,
            @Parameter(description = "생활기록부 항목")
            @RequestParam(required = false) final List<String> recordTypes,
            @Parameter(description = "마지막 조회된 학생 ID (페이징)")
            @RequestParam(required = false) final Long lastStudentId
    );
}
