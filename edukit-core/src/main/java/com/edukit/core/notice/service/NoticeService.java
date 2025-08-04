package com.edukit.core.notice.service;

import com.edukit.core.notice.db.entity.Notice;
import com.edukit.core.notice.db.entity.NoticeFile;
import com.edukit.core.notice.db.enums.NoticeCategory;
import com.edukit.core.notice.exception.NoticeException;
import com.edukit.core.notice.exception.NoticeErrorCode;
import com.edukit.core.notice.db.repository.NoticeFileRepository;
import com.edukit.core.notice.db.repository.NoticeRepository;
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
    public Notice createNotice(final NoticeCategory category, final String title, final String content) {
        validateCategory(category);
        Notice notice = Notice.create(category, title, content);
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice updateNotice(final long noticeId, final NoticeCategory category, final String title, final String content) {
        validateCategory(category);
        Notice notice = getNotice(noticeId);
        notice.update(category, title, content);
        return notice;
    }

    @Transactional
    public void deleteNotice(final long noticeId) {
        Notice notice = getNotice(noticeId);
        notice.delete();
    }

    @Transactional
    public List<NoticeFile> createNoticeFiles(final List<String> filePaths) {
        List<NoticeFile> noticeFiles = filePaths.stream().map(NoticeFile::create).toList();
        return noticeFileRepository.saveAll(noticeFiles);
    }

    @Transactional
    public void updateNoticeFilesNoticeId(final List<Long> noticeFileIds, final Notice notice) {
        List<NoticeFile> noticeFiles = noticeFileRepository.findAllById(noticeFileIds);
        for (NoticeFile noticeFile : noticeFiles) {
            noticeFile.attachToNotice(notice);
        }
    }

    public List<NoticeFile> getNoticeFiles(final Notice notice) {
        return noticeFileRepository.findByNotice(notice);
    }

    @Transactional
    public void updateNoticeFiles(final Notice notice, final List<Long> noticeFileIds) {
        List<NoticeFile> beforeNoticeFiles = getNoticeFiles(notice);
        List<NoticeFile> updatedNoticeFiles = noticeFileRepository.findAllById(noticeFileIds);
        for (NoticeFile beforeNoticeFile : beforeNoticeFiles) {
            if (beforeNoticeFile.isExcludedFrom(updatedNoticeFiles)) {
                beforeNoticeFile.detachNotice();
            }
        }
        for (NoticeFile updatedNoticeFile : updatedNoticeFiles) {
            if (updatedNoticeFile.isDetachedFromNotice()) {
                updatedNoticeFile.attachToNotice(notice);
            }
        }
    }

    private void validateCategory(final NoticeCategory category) {
        if (category == null || !category.isCreatable()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_CATEGORY);
        }
    }
}
