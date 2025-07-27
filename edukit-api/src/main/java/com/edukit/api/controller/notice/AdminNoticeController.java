package com.edukit.api.controller.notice;

import com.edukit.api.common.ApiResponse;
import com.edukit.api.controller.notice.request.NoticeCreateRequest;
import com.edukit.api.controller.notice.request.NoticeUpdateRequest;
import com.edukit.common.exception.code.CommonSuccessCode;
import com.edukit.core.notice.enums.NoticeCategory;
import com.edukit.core.notice.facade.NoticeFacade;
import com.edukit.core.notice.facade.response.NoticeFileUploadPresignedUrlCreateResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeFacade noticeFacade;

    @PostMapping
    public ApiResponse<Void> createNotice(
            @RequestBody @Valid final NoticeCreateRequest request
    ) {
        NoticeCategory category = NoticeCategory.fromId(request.categoryId());
        noticeFacade.createNotice(category, request.title(), request.content(), request.noticeFileIds());
        return ApiResponse.success(CommonSuccessCode.OK);
    }

    @PatchMapping("/{noticeId}")
    public ApiResponse<Void> updateNotice(
            @RequestBody @Valid final NoticeUpdateRequest request,
            @PathVariable final long noticeId
    ) {
        NoticeCategory category = NoticeCategory.fromId(request.categoryId());
        noticeFacade.updateNotice(noticeId, category, request.title(), request.content(), request.noticeFileIds());
        return ApiResponse.success(CommonSuccessCode.OK);
    }

    @DeleteMapping("/{noticeId}")
    public ApiResponse<Void> deleteNotice(
            @PathVariable final long noticeId
    ) {
        noticeFacade.deleteNotice(noticeId);
        return ApiResponse.success(CommonSuccessCode.OK);
    }

    @GetMapping("/presigned-url")
    public ApiResponse<NoticeFileUploadPresignedUrlCreateResponse> createFileUploadPresignedUrl(
            @RequestParam final List<String> filenames
    ) {
        return ApiResponse.success(CommonSuccessCode.OK, noticeFacade.createFileUploadPresignedUrl(filenames));
    }
}
