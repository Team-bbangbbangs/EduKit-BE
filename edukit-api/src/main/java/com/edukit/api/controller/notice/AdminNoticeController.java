package com.edukit.api.controller.notice;

import com.edukit.api.common.EdukitResponse;
import com.edukit.api.controller.notice.request.NoticeCreateRequest;
import com.edukit.api.controller.notice.request.NoticeUpdateRequest;
import com.edukit.core.notice.enums.NoticeCategory;
import com.edukit.core.notice.facade.NoticeFacade;
import com.edukit.core.notice.facade.response.NoticeFileUploadPresignedUrlCreateResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<EdukitResponse<Void>> createNotice(
            @RequestBody @Valid final NoticeCreateRequest request
    ) {
        NoticeCategory category = NoticeCategory.fromId(request.categoryId());
        noticeFacade.createNotice(category, request.title(), request.content(), request.noticeFileIds());
        return ResponseEntity.ok().body(EdukitResponse.success());
    }

    @PatchMapping("/{noticeId}")
    public ResponseEntity<EdukitResponse<Void>> updateNotice(
            @RequestBody @Valid final NoticeUpdateRequest request,
            @PathVariable final long noticeId
    ) {
        NoticeCategory category = NoticeCategory.fromId(request.categoryId());
        noticeFacade.updateNotice(noticeId, category, request.title(), request.content(), request.noticeFileIds());
        return ResponseEntity.ok().body(EdukitResponse.success());
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<EdukitResponse<Void>> deleteNotice(
            @PathVariable final long noticeId
    ) {
        noticeFacade.deleteNotice(noticeId);
        return ResponseEntity.ok().body(EdukitResponse.success());
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<EdukitResponse<NoticeFileUploadPresignedUrlCreateResponse>> createFileUploadPresignedUrl(
            @RequestParam final List<String> filenames
    ) {
        return ResponseEntity.ok().body(EdukitResponse.success(noticeFacade.createFileUploadPresignedUrl(filenames)));
    }
}
