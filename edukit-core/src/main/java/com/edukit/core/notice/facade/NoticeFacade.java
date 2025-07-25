package com.edukit.core.notice.facade;

import com.edukit.core.notice.entity.Notice;
import com.edukit.core.notice.enums.NoticeCategory;
import com.edukit.core.notice.facade.response.NoticeGetResponse;
import com.edukit.core.notice.facade.response.NoticeImageUploadPresignedUrlCreateResponse;
import com.edukit.core.notice.facade.response.NoticeResponse;
import com.edukit.core.notice.facade.response.NoticesGetResponse;
import com.edukit.core.notice.service.NoticeService;
import com.edukit.external.s3.dto.S3Service;
import com.edukit.external.s3.dto.response.PresignedUrlCreateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeFacade {

    private final NoticeService noticeService;
    private final S3Service s3Service;
    private static final String NOTICE_IMAGE_PATH = "notices";

    public NoticesGetResponse getNotices(final NoticeCategory category, final int page) {
        int zeroBasedPage = page - 1;
        Page<Notice> noticePages = noticeService.getNotices(category, zeroBasedPage);
        List<NoticeResponse> notices = noticePages.getContent().stream()
                .map(notice -> new NoticeResponse(notice.getId(), notice.getCategory().getText(), notice.getTitle(),
                        notice.getCreatedAt())).toList();
        return NoticesGetResponse.of(
                noticePages.getTotalPages(),
                notices
        );
    }

    public NoticeGetResponse getNotice(final long noticeId) {
        Notice notice = noticeService.getNotice(noticeId);
        return NoticeGetResponse.of(
                notice.getId(),
                notice.getCategory().getText(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt()
        );
    }

    public void createNotice(final NoticeCategory category, final String title, final String content) {
        noticeService.createNotice(category, title, content);
    }

    public void updateNotice(final long noticeId, final NoticeCategory category, final String title, final String content) {
        noticeService.updateNotice(noticeId, category, title, content);
    }

    public void deleteNotice(long noticeId) {
        noticeService.deleteNotice(noticeId);
    }

    public NoticeImageUploadPresignedUrlCreateResponse createImageUploadPresignedUrl(String filename) {
        PresignedUrlCreateResponse response = s3Service.createPresignedUrl(NOTICE_IMAGE_PATH, filename);
        return new NoticeImageUploadPresignedUrlCreateResponse(response.presignedUrl(), response.imageUrl());
    }
}
