package com.edukit.notice.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.notice.facade.response.NoticeGetResponse;
import com.edukit.notice.facade.response.NoticesGetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "공지사항", description = "공지사항 관련 API")
public interface NoticeApi {
    @Operation(summary = "공지사항 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 카테고리 ID",
                                            description = "유효하지 않은 공지사항 카테고리 ID를 입력한 경우",
                                            value = """
                                                      {
                                                        "code": "NO-40001",
                                                        "message": "유효하지 않은 공지사항 카테고리입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "페이지 번호 validation 오류",
                                            description = "페이지 번호가 1보다 작은 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "page": "1 이상이어야 합니다"
                                                        }
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<NoticesGetResponse>> getNotices(
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "page", required = false, defaultValue = "1") @Min(1) int page
    );

    @Operation(summary = "공지사항 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "공지사항 미존재",
                                            description = "해당 ID의 공지사항이 존재하지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "NO-40402",
                                                        "message": "해당 공지사항이 존재하지 않습니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<NoticeGetResponse>> getNotice(
            @PathVariable final long noticeId
    );
}
