package com.edukit.api.notice.facade;

import com.edukit.api.notice.facade.response.NoticeFileUploadPresignedUrlCreateResponse;
import com.edukit.api.notice.facade.response.NoticeFileUploadPresignedUrlCreateResponse.NoticeFileUploadPresignedUrlCreateResponseItem;
import com.edukit.api.notice.facade.response.NoticeGetResponse;
import com.edukit.api.notice.facade.response.NoticeResponse;
import com.edukit.api.notice.facade.response.NoticesGetResponse;
import com.edukit.core.common.service.FileStorageService;
import com.edukit.core.common.service.response.UploadPresignedUrlResponse;
import com.edukit.core.notice.db.entity.Notice;
import com.edukit.core.notice.db.entity.NoticeFile;
import com.edukit.core.notice.db.enums.NoticeCategory;
import com.edukit.core.notice.service.NoticeFileService;
import com.edukit.core.notice.service.NoticeService;
import com.edukit.core.notice.service.dto.NoticeCreateResult;
import com.edukit.core.notice.service.dto.NoticeUpdateResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(FileStorageService.class)
public class NoticeFacade {

    private final NoticeService noticeService;
    private final NoticeFileService noticeFileService;
    private final FileStorageService storageService;
    private static final String TMP_NOTICE_FILE_DIRECTORY = "tmp";
    private static final String NOTICE_FILE_DIRECTORY = "notices";

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
        List<NoticeFile> noticeFiles = noticeFileService.getNoticeFiles(notice);
        return NoticeGetResponse.of(
                notice.getId(),
                notice.getCategory().getText(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                noticeFiles.stream().map(NoticeFile::getId).toList()
        );
    }

    public void createNotice(final NoticeCategory category, final String title, final String content,
                             final List<String> fileKeys) {
        NoticeCreateResult result = noticeService.createNoticeAndFiles(category, title, content, fileKeys);
        if (result.hasFiles()) {
            storageService.moveFiles(result.fileKeys(), TMP_NOTICE_FILE_DIRECTORY, NOTICE_FILE_DIRECTORY);
        }
    }

    public void updateNotice(final long noticeId, final NoticeCategory category, final String title,
                             final String content, final List<String> addedFileKeys,
                             final List<Long> deletedNoticeFileIds) {
        NoticeUpdateResult result = noticeService.updateNoticeAndFiles(
                noticeId, category, title, content, addedFileKeys, deletedNoticeFileIds);
        if (result.hasAddedFiles()) {
            storageService.moveFiles(result.addedFileKeys(), TMP_NOTICE_FILE_DIRECTORY, NOTICE_FILE_DIRECTORY);
        }
        if (result.hasDeletedFiles()) {
            storageService.deleteFiles(result.deletedFileKeys());
        }
    }

    public void deleteNotice(final long noticeId) {
        // 공지사항 복구 시 파일 복원 가능해야 하므로 사진은 삭제하지 않음
        noticeService.deleteNotice(noticeId);
    }

    public NoticeFileUploadPresignedUrlCreateResponse createFileUploadPresignedUrl(final List<String> filenames) {
        List<UploadPresignedUrlResponse> presignedUrls = filenames.stream()
                .map(filename -> storageService.createUploadPresignedUrl(TMP_NOTICE_FILE_DIRECTORY, filename))
                .toList();
        List<NoticeFileUploadPresignedUrlCreateResponseItem> files = presignedUrls.stream()
                .map(response -> NoticeFileUploadPresignedUrlCreateResponseItem.of(response, NOTICE_FILE_DIRECTORY))
                .toList();
        return NoticeFileUploadPresignedUrlCreateResponse.of(files);
    }
}
