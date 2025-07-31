package com.edukit.api.controller.notice;

import com.edukit.api.common.EdukitResponse;
import com.edukit.api.controller.notice.request.NoticeCreateRequest;
import com.edukit.api.controller.notice.request.NoticeUpdateRequest;
import com.edukit.core.notice.facade.response.NoticeFileUploadPresignedUrlCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "관리자 공지사항", description = "관리자 공지사항 관리 API")
public interface AdminNoticeApi {
    @Operation(summary = "공지사항 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "카테고리 ID, 제목, 내용 중 하나라도 null인 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "categoryId": "카테고리 ID는 필수입니다.",
                                                          "title": "제목은 필수입니다.",
                                                          "content": "내용은 필수입니다."
                                                        }
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "잘못된 카테고리 ID",
                                            description = "유효하지 않은 공지사항 카테고리 ID를 입력한 경우",
                                            value = """
                                                      {
                                                        "code": "NO-40001",
                                                        "message": "유효하지 않은 공지사항 카테고리입니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> createNotice(
            @RequestBody @Valid final NoticeCreateRequest request
    );

    @Operation(summary = "공지사항 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "카테고리 ID, 제목, 내용 중 하나라도 null인 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "categoryId": "카테고리 ID는 필수입니다.",
                                                          "title": "제목은 필수입니다.",
                                                          "content": "내용은 필수입니다."
                                                        }
                                                      }
                                                    """
                                    ),
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
                                            name = "공지사항 미존재",
                                            description = "수정하려는 공지사항이 존재하지 않는 경우",
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
    ResponseEntity<EdukitResponse<Void>> updateNotice(
            @RequestBody @Valid final NoticeUpdateRequest request,
            @PathVariable final long noticeId
    );

    @Operation(summary = "공지사항 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "공지사항 미존재",
                                            description = "삭제하려는 공지사항이 존재하지 않는 경우",
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
    ResponseEntity<EdukitResponse<Void>> deleteNotice(
            @PathVariable final long noticeId
    );

    @Operation(summary = "파일 업로드용 Presigned URL 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 파일명",
                                            description = "파일명이 비어있거나 확장자가 없는 경우",
                                            value = """
                                                      {
                                                        "code": "S3-40001",
                                                        "message": "잘못된 파일 이름입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "지원하지 않는 파일 확장자",
                                            description = "이미지 파일이 아닌 경우 (png, jpg, jpeg, gif, webp만 지원)",
                                            value = """
                                                      {
                                                        "code": "S3-40002",
                                                        "message": "지원하지 않는 파일 확장자입니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<NoticeFileUploadPresignedUrlCreateResponse>> createFileUploadPresignedUrl(
            @RequestParam final List<String> filenames
    );
}
