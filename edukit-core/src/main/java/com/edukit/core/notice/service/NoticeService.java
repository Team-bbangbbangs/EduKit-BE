package com.edukit.core.notice.service;

import com.edukit.core.notice.db.entity.Notice;
import com.edukit.core.notice.db.enums.NoticeCategory;
import com.edukit.core.notice.db.repository.NoticeRepository;
import com.edukit.core.notice.exception.NoticeErrorCode;
import com.edukit.core.notice.exception.NoticeException;
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
    public void createNotice(final NoticeCategory category, final String title, final String content) {
        validateCategory(category);
        Notice notice = Notice.create(category, title, content);
        noticeRepository.save(notice);
    }

    @Transactional
    public void updateNotice(final long noticeId, final NoticeCategory category, final String title, final String content) {
        validateCategory(category);
        Notice notice = getNotice(noticeId);
        notice.update(category, title, content);
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
}
