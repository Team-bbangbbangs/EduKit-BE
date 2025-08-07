package com.edukit.core.notice.service;

import com.edukit.core.notice.db.entity.Notice;
import com.edukit.core.notice.db.entity.NoticeFile;
import com.edukit.core.notice.db.repository.NoticeFileRepository;
import com.edukit.core.notice.exception.NoticeErrorCode;
import com.edukit.core.notice.exception.NoticeException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeFileService {

    private final NoticeFileRepository noticeFileRepository;

    public List<NoticeFile> getNoticeFiles(final Notice notice) {
        return noticeFileRepository.findByNotice(notice);
    }

    @Transactional
    public List<NoticeFile> createNoticeFiles(final List<String> fileKeys, final Notice notice) {
        List<NoticeFile> noticeFiles = fileKeys.stream().map(fileKey -> NoticeFile.create(notice, fileKey)).toList();
        return noticeFileRepository.saveAll(noticeFiles);
    }

    @Transactional
    public List<NoticeFile> deleteNoticeFiles(final List<Long> noticeFileIds, final Notice notice) {
        List<NoticeFile> noticeFiles = noticeFileRepository.findByNoticeAndIdIn(notice, noticeFileIds);
        if (noticeFiles.size() != noticeFileIds.size()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_FILE_IDS);
        }
        noticeFileRepository.deleteAll(noticeFiles);
        return noticeFiles;
    }
}
