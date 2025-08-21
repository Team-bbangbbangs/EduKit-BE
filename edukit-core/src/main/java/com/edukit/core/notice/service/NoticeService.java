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
    public NoticeUpdateResult updateNoticeAndFiles(final long noticeId, final NoticeCategory category,
                                                   final String title, final String content,
                                                   final List<String> fileKeys) {
        Notice notice = updateNotice(noticeId, category, title, content);

        List<NoticeFile> existNoticeFiles = getNoticeFiles(notice);
        //파일 추가
        List<String> existNoticeFileKeys = existNoticeFiles.stream().map(NoticeFile::getFileKey).toList();
        List<String> addedNoticeFileKeys = fileKeys.stream().filter(fileKey -> !existNoticeFileKeys.contains(fileKey))
                .toList();
        List<NoticeFile> addedNoticeFiles = createNoticeFiles(addedNoticeFileKeys, notice);

        //파일 삭제
        List<NoticeFile> deletedNoticeFiles = existNoticeFiles.stream()
                .filter(noticeFile -> !fileKeys.contains(noticeFile.getFileKey())).toList();
        deleteNoticeFiles(deletedNoticeFiles);

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

    private void deleteNoticeFiles(final List<NoticeFile> deletedNoticeFiles) {
        if (deletedNoticeFiles.isEmpty()) {
            return;
        }
        noticeFileRepository.deleteAllByIdInBatch(deletedNoticeFiles.stream().map(NoticeFile::getId).toList());
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
