package com.edukit.core.notice.service.dto;

import com.edukit.core.notice.db.entity.NoticeFile;
import java.util.List;

public record NoticeCreateResult(
        List<String> fileKeys
) {

    public static NoticeCreateResult of(final List<NoticeFile> noticeFiles) {
        List<String> fileKeys = noticeFiles.stream().map(NoticeFile::getFileKey).toList();
        return new NoticeCreateResult(fileKeys);
    }

    public boolean hasFiles() {
        return fileKeys != null && !fileKeys.isEmpty();
    }
}
