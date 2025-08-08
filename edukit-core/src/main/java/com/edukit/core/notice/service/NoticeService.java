package com.edukit.core.notice.service;

import com.edukit.core.notice.db.entity.Notice;
import com.edukit.core.notice.db.entity.NoticeFile;
import com.edukit.core.notice.db.enums.NoticeCategory;
import com.edukit.core.notice.db.repository.NoticeFileRepository;
import com.edukit.core.notice.db.repository.NoticeRepository;
import com.edukit.core.notice.exception.NoticeErrorCode;
import com.edukit.core.notice.exception.NoticeException;
import com.edukit.core.notice.service.dto.NoticeCreateResult;
import com.edukit.core.notice.service.dto.NoticeUpdateResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;

    private static final int PAGE_SIZE = 10;

    public Page<Notice> getNotices(final NoticeCategory category, final int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        if (category == NoticeCategory.ALL) {
            return noticeRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return noticeRepository.findAllByCategoryOrderByCreatedAtDesc(category, pageable);
    }

    public Notice getNotice(final long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND));
    }

    @Transactional
    public NoticeCreateResult createNoticeAndFiles(final NoticeCategory category, final String title,
                                                   final String content,
                                                   final List<String> fileKeys) {
        Notice notice = createNotice(category, title, content);
        List<NoticeFile> noticeFiles = createNoticeFiles(fileKeys, notice);

        return NoticeCreateResult.of(noticeFiles);
    }

    @Transactional
    public NoticeUpdateResult updateNoticeAndFiles(final long noticeId, final NoticeCategory category, final String title,
                                           final String content,
                                           final List<String> addedFileKeys, final List<Long> deletedNoticeFileIds) {
        Notice notice = updateNotice(noticeId, category, title, content);
        List<NoticeFile> addedNoticeFiles = createNoticeFiles(addedFileKeys, notice);
        List<NoticeFile> deletedNoticeFiles = deleteNoticeFiles(deletedNoticeFileIds, notice);

        return NoticeUpdateResult.of(addedNoticeFiles, deletedNoticeFiles);
    }

    private Notice createNotice(final NoticeCategory category, final String title, final String content) {
        validateCategory(category);
        Notice notice = Notice.create(category, title, content);
        noticeRepository.save(notice);
        return notice;
    }

    private Notice updateNotice(final long noticeId, final NoticeCategory category, final String title,
                                final String content) {
        Notice notice = getNotice(noticeId);
        validateCategory(category);
        notice.update(category, title, content);
        return notice;
    }

    private List<NoticeFile> createNoticeFiles(final List<String> fileKeys, final Notice notice) {
        if (fileKeys.isEmpty()) {
            return List.of();
        }
        List<NoticeFile> noticeFiles = fileKeys.stream().map(fileKey -> NoticeFile.create(notice, fileKey)).toList();
        noticeFileRepository.saveAll(noticeFiles);
        return noticeFiles;
    }

    private List<NoticeFile> deleteNoticeFiles(final List<Long> noticeFileIds, final Notice notice) {
        if (noticeFileIds.isEmpty()) {
            return List.of();
        }
        List<NoticeFile> noticeFiles = noticeFileRepository.findByNoticeAndIdIn(notice, noticeFileIds);
        if (noticeFiles.size() != noticeFileIds.size()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_FILE_IDS);
        }
        noticeFileRepository.deleteAll(noticeFiles);
        return noticeFiles;
    }

    @Transactional
    public void deleteNotice(final long noticeId) {
        Notice notice = getNotice(noticeId);
        notice.delete();
    }

    private void validateCategory(final NoticeCategory category) {
        if (category == null || !category.isCreatable()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_CATEGORY);
        }
    }

    public List<NoticeFile> getNoticeFiles(final Notice notice) {
        return noticeFileRepository.findByNotice(notice);
    }
}
