package com.edukit.admin.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.admin.controller.request.NoticeCreateRequest;
import com.edukit.admin.controller.request.NoticeUpdateRequest;
import com.edukit.notice.facade.response.NoticeFileUploadPresignedUrlCreateResponse;
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
public interface AdminApi {
    @Operation(
            summary = "공지사항 생성",
            description = """
                    **📝 공지사항 생성 워크플로우**
                    
                    **1단계: 파일 업로드 (선택사항)**
                    - 공지사항에 이미지를 포함하려면 먼저 /presigned-url API로 업로드 URL을 발급받습니다
                    - 발급받은 presigned URL로 S3에 직접 파일을 업로드합니다
                    
                    **2단계: 공지사항 생성**
                    - category: 공지사항 카테고리 (announcement: 공지, event: 이벤트)
                    - title: 공지사항 제목
                    - content: 공지사항 본문 내용
                    - fileKeys: **실제 본문에 포함된 이미지의 fileKey만 포함** (업로드한 모든 파일이 아님)
                    
                    **⚠️ 중요사항**
                    - 여러 파일을 업로드했더라도 fileKeys에는 실제 본문에 사용된 이미지만 포함하세요
                    - 사용하지 않은 업로드 파일은 자동으로 정리됩니다
                    """
    )
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
                                                          "category": "카테고리는 필수입니다.",
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

    @Operation(
            summary = "공지사항 수정",
            description = """
                    **✏️ 공지사항 수정 워크플로우**
                    
                    **1단계: 기존 공지사항 조회**
                    - GET /api/v2/notices/{noticeId}로 기존 공지사항 정보와 첨부파일 목록을 확인합니다
                    
                    **2단계: 새 파일 업로드 (필요시)**
                    - 새로 추가할 이미지가 있다면 /presigned-url API로 업로드 URL을 발급받습니다
                    - 발급받은 presigned URL로 S3에 직접 파일을 업로드합니다
                    
                    **3단계: 공지사항 수정**
                    - category, title, content: 공지사항 정보
                    - addedFileKeys: **새로 추가된 이미지의 fileKey 목록** (실제 본문에 포함된 것만)
                    - deletedNoticeFileIds: **삭제할 기존 파일의 ID 목록** (1단계에서 조회한 파일 ID)
                    
                    **⚠️ 중요사항**
                    - addedFileKeys: 새로 업로드한 파일 중 실제 본문에 사용된 것만 포함
                    - deletedNoticeFileIds: 기존 파일 중 삭제할 파일의 DB ID (fileKey가 아님)
                    - 두 필드 모두 선택사항이며 각각 독립적으로 처리됩니다
                    """
    )
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
                                                          "category": "카테고리는 필수입니다.",
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
                                    ),
                                    @ExampleObject(
                                            name = "잘못된 파일 ID",
                                            description = "삭제 요청한 파일 중 해당 공지사항에 속하지 않는 파일이 있는 경우",
                                            value = """
                                                      {
                                                        "code": "NO-40003",
                                                        "message": "삭제 요청한 파일 중 해당 공지사항에 속하지 않는 파일이 있습니다."
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

    @Operation(
            summary = "파일 업로드용 Presigned URL 생성",
            description = """
                    **🔗 파일 업로드 Presigned URL 발급**
                    
                    **사용 목적**
                    - 공지사항에 첨부할 이미지 파일을 S3에 업로드하기 위한 임시 URL을 발급받습니다
                    - 클라이언트에서 서버를 거치지 않고 S3에 직접 업로드할 수 있습니다
                    
                    **요청 방법**
                    - filenames: 업로드할 파일명 목록 (확장자 포함)
                    - 예: /presigned-url?filenames=image1.jpg, image2.png
                    
                    **응답 정보**
                    - uploadPresignedUrl: S3 업로드용 임시 URL (PUT 요청 사용, 5분간 유효)
                    - tmpFileUrl: 업로드 후 임시 파일 접근 URL
                    - fileUrl: 공지사항 생성 후 최종 파일 URL (해당 주소를 본문에 포함)
                    - fileKey: 공지사항 생성/수정 시 사용할 파일 키
                    
                    **지원 파일 형식**
                    - 이미지 파일만 지원: png, jpg, jpeg, gif, webp
                    
                    **워크플로우**
                    1. 이 API로 presigned URL 발급
                    2. 발급받은 URL로 S3에 직접 업로드 (PUT 요청)
                    3. 공지사항 생성/수정 시 fileKey 값을 사용
                    """
    )
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
