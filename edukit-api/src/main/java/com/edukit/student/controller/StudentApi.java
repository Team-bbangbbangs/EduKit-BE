package com.edukit.student.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "학생", description = "학생 관련 API")
public interface StudentApi {

    @Operation(summary = "Excel 파일로 학생 일괄 업로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<EdukitResponse<Void>> uploadStudentExcel(
            @MemberId long memberId,
            @RequestParam("file") final MultipartFile file
    );
}
