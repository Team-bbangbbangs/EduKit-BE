package com.edukit.core.notice.facade;

import com.edukit.core.notice.entity.Notice;
import com.edukit.core.notice.entity.NoticeFile;
import com.edukit.core.notice.enums.NoticeCategory;
import com.edukit.core.notice.facade.response.NoticeFileUploadPresignedUrlCreateResponse;
import com.edukit.core.notice.facade.response.NoticeGetResponse;
import com.edukit.core.notice.facade.response.NoticeResponse;
import com.edukit.core.notice.facade.response.NoticesGetResponse;
import com.edukit.core.notice.service.NoticeService;
import com.edukit.external.aws.s3.S3Service;
import com.edukit.external.aws.s3.response.UploadPresignedUrlResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeFacade {

    private final NoticeService noticeService;
    private final S3Service s3Service;
    private static final String NOTICE_FILE_PATH = "notices";

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
        List<NoticeFile> noticeFiles = noticeService.getNoticeFiles(notice);
        return NoticeGetResponse.of(
                notice.getId(),
                notice.getCategory().getText(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                noticeFiles.stream().map(NoticeFile::getId).toList()
        );
    }

    @Transactional
    public void createNotice(final NoticeCategory category, final String title, final String content,
                             final List<Long> noticeFileIds) {
        Notice notice = noticeService.createNotice(category, title, content);
        if (!noticeFileIds.isEmpty()) {
            noticeService.updateNoticeFilesNoticeId(noticeFileIds, notice);
        }
    }

    @Transactional
    public void updateNotice(final long noticeId, final NoticeCategory category, final String title,
                             final String content, final List<Long> noticeFileIds) {
        Notice notice = noticeService.updateNotice(noticeId, category, title, content);
        noticeService.updateNoticeFiles(notice, noticeFileIds);
    }

    // 공지사항 복구 시 파일 복원 가능해야 하므로 NoticeFile은 삭제하지 않음
    public void deleteNotice(final long noticeId) {
        noticeService.deleteNotice(noticeId);
    }

    public NoticeFileUploadPresignedUrlCreateResponse createFileUploadPresignedUrl(final List<String> filenames) {
        List<UploadPresignedUrlResponse> presignedUrls = filenames.stream()
                .map(filename -> s3Service.createUploadPresignedUrl(NOTICE_FILE_PATH, filename))
                .toList();
        List<NoticeFile> noticeFiles = noticeService.createNoticeFiles(
                presignedUrls.stream().map(UploadPresignedUrlResponse::s3Key).toList()
        );
        return NoticeFileUploadPresignedUrlCreateResponse.of(presignedUrls, noticeFiles);
    }
}
