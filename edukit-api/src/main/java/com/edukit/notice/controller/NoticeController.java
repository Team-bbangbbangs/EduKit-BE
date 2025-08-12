package com.edukit.notice.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.core.notice.db.enums.NoticeCategory;
import com.edukit.notice.facade.NoticeFacade;
import com.edukit.notice.facade.response.NoticeGetResponse;
import com.edukit.notice.facade.response.NoticesGetResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController implements NoticeApi {

    private final NoticeFacade noticeFacade;

    @GetMapping
    public ResponseEntity<EdukitResponse<NoticesGetResponse>> getNotices(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "page", required = false, defaultValue = "1") @Min(1) int page
    ) {
        NoticeCategory noticeCategory = NoticeCategory.from(category);
        return ResponseEntity.ok().body(EdukitResponse.success(noticeFacade.getNotices(noticeCategory, page)));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<EdukitResponse<NoticeGetResponse>> getNotice(
            @PathVariable final long noticeId
    ) {
        return ResponseEntity.ok().body(EdukitResponse.success(noticeFacade.getNotice(noticeId)));
    }
}
