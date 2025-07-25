package com.edukit.api.notice.controller;

import com.edukit.api.common.ApiResponse;
import com.edukit.common.exception.code.CommonSuccessCode;
import com.edukit.core.notice.enums.NoticeCategory;
import com.edukit.core.notice.facade.NoticeFacade;
import com.edukit.core.notice.facade.response.NoticeGetResponse;
import com.edukit.core.notice.facade.response.NoticesGetResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeFacade noticeFacade;

    @GetMapping
    public ApiResponse<NoticesGetResponse> getNotices(
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "page", required = false, defaultValue = "1") @Min(1) int page
    ) {
        NoticeCategory category = NoticeCategory.fromId(categoryId);
        return ApiResponse.success(
                CommonSuccessCode.OK,
                noticeFacade.getNotices(category, page)
        );
    }

    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeGetResponse> getNotice(
            @PathVariable final long noticeId
    ) {
        return ApiResponse.success(
                CommonSuccessCode.OK,
                noticeFacade.getNotice(noticeId)
        );
    }
}
