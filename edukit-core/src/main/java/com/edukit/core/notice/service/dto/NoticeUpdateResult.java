package com.edukit.core.notice.service.dto;

import com.edukit.core.notice.db.entity.NoticeFile;
import java.util.List;

public record NoticeUpdateResult(
        List<String> addedFileKeys,
        List<String> deletedFileKeys
) {
    public static NoticeUpdateResult of(final List<NoticeFile> addedNoticeFiles, final List<NoticeFile> deletedNoticeFiles) {
        List<String> addedFileKeys = addedNoticeFiles.stream().map(NoticeFile::getFileKey).toList();
        List<String> deletedFileKeys = deletedNoticeFiles.stream().map(NoticeFile::getFileKey).toList();
        return new NoticeUpdateResult(addedFileKeys, deletedFileKeys);
    }

    public boolean hasAddedFiles() {
        return addedFileKeys != null && !addedFileKeys.isEmpty();
    }

    public boolean hasDeletedFiles() {
        return deletedFileKeys != null && !deletedFileKeys.isEmpty();
    }
}
