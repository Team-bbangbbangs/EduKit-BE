package com.edukit.core.notice.facade.response;

import com.edukit.core.notice.entity.NoticeFile;
import com.edukit.external.s3.dto.response.PresignedUrlCreateResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record NoticeFileUploadPresignedUrlCreateResponse(
        List<NoticeFileUploadItem> items
) {
    public record NoticeFileUploadItem(
            long noticeFileId,
            String presignedUrl,
            String fileUrl
    ) {
        public static NoticeFileUploadItem of(final long noticeFileId, final String presignedUrl, String fileUrl) {
            return new NoticeFileUploadItem(noticeFileId, presignedUrl, fileUrl);
        }
    }

    public static NoticeFileUploadPresignedUrlCreateResponse of(List<PresignedUrlCreateResponse> presignedUrls, List<NoticeFile> noticeFiles) {
        Map<String, NoticeFile> filePathToNoticeFile = noticeFiles.stream()
                .collect(Collectors.toMap(NoticeFile::getFilePath, Function.identity()));
        List<NoticeFileUploadItem> items = presignedUrls.stream()
                .map(urlResponse -> {
                    NoticeFile noticeFile = filePathToNoticeFile.get(urlResponse.filePath());
                    return NoticeFileUploadItem.of(noticeFile.getId(), urlResponse.presignedUrl(), urlResponse.fileUrl());
                })
                .toList();
        return new NoticeFileUploadPresignedUrlCreateResponse(items);
    }
}
