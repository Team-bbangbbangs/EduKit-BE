package com.edukit.api.notice.facade;

import com.edukit.api.notice.facade.response.NoticeFileUploadPresignedUrlCreateResponse;
import com.edukit.api.notice.facade.response.NoticeFileUploadPresignedUrlCreateResponse.NoticeFileUploadPresignedUrlCreateResponseItem;
import com.edukit.api.notice.facade.response.NoticeGetResponse;
import com.edukit.api.notice.facade.response.NoticeResponse;
import com.edukit.api.notice.facade.response.NoticesGetResponse;
import com.edukit.core.common.service.FileStorageService;
import com.edukit.core.common.service.response.UploadPresignedUrlResponse;
import com.edukit.core.notice.db.entity.Notice;
import com.edukit.core.notice.db.enums.NoticeCategory;
import com.edukit.core.notice.service.NoticeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(FileStorageService.class)
public class NoticeFacade {

    private final NoticeService noticeService;
    private final FileStorageService storageService;
    private static final String TMP_NOTICE_FILE_PATH = "tmp";
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
        return NoticeGetResponse.of(
                notice.getId(),
                notice.getCategory().getText(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt()
        );
    }

    @Transactional
    public void createNotice(final NoticeCategory category, final String title, final String content,
                             final List<String> imageUrls) {
        noticeService.createNotice(category, title, content);
        if (!CollectionUtils.isEmpty(imageUrls)) {     //사진이 포함된 본문인 경우
            storageService.moveFiles(imageUrls, TMP_NOTICE_FILE_PATH, NOTICE_FILE_PATH);
        }
    }

    @Transactional
    public void updateNotice(final long noticeId, final NoticeCategory category, final String title,
                             final String content, final List<String> addedImageUrls,
                             final List<String> deletedImageUrls) {
        noticeService.updateNotice(noticeId, category, title, content);
        if (!CollectionUtils.isEmpty(addedImageUrls)) {
            storageService.moveFiles(addedImageUrls, TMP_NOTICE_FILE_PATH, NOTICE_FILE_PATH);
        }
        if (!CollectionUtils.isEmpty(deletedImageUrls)) {
            storageService.deleteFiles(deletedImageUrls);
        }
    }

    public void deleteNotice(final long noticeId) {
        // 공지사항 복구 시 파일 복원 가능해야 하므로 사진은 삭제하지 않음
        noticeService.deleteNotice(noticeId);
    }

    public NoticeFileUploadPresignedUrlCreateResponse createFileUploadPresignedUrl(final List<String> filenames) {
        List<UploadPresignedUrlResponse> presignedUrls = filenames.stream()
                .map(filename -> storageService.createUploadPresignedUrl(TMP_NOTICE_FILE_PATH, filename))
                .toList();
        List<NoticeFileUploadPresignedUrlCreateResponseItem> images = presignedUrls.stream()
                .map(response -> NoticeFileUploadPresignedUrlCreateResponseItem.of(response,
                        getNoticeImageUrl(response.baseUrl(), response.fileName())))
                .toList();
        return NoticeFileUploadPresignedUrlCreateResponse.of(images);
    }

    private String getNoticeImageUrl(final String baseUrl, final String fileName) {
        return String.format("%s/%s/%s", baseUrl, NOTICE_FILE_PATH, fileName);
    }
}
