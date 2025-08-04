package com.edukit.api.notice.facade.response;

import com.edukit.core.common.service.response.UploadPresignedUrlResponse;
import com.edukit.core.notice.db.entity.NoticeFile;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record NoticeFileUploadPresignedUrlCreateResponse(
        List<NoticeFileUploadPresignedUrlCreateResponseItem> items
) {
    public record NoticeFileUploadPresignedUrlCreateResponseItem(long noticeFileId, String uploadPresignedUrl,
                                                                 //업로드용 주소
                                                                 String fileUrl
                                                                 //조회용 주소 (https://dev-cdn.edukit.co.kr/notices/20250725_223935_ea5f18be.jpg)
    ) {
        public static NoticeFileUploadPresignedUrlCreateResponseItem of(final long noticeFileId,
                                                                        final String presignedUrl, String fileUrl) {
            return new NoticeFileUploadPresignedUrlCreateResponseItem(noticeFileId, presignedUrl, fileUrl);
        }
    }

    public static NoticeFileUploadPresignedUrlCreateResponse of(final List<UploadPresignedUrlResponse> presignedUrls,
                                                                final List<NoticeFile> noticeFiles) {
        Map<String, NoticeFile> filePathToNoticeFile = noticeFiles.stream()
                .collect(Collectors.toMap(NoticeFile::getFilePath, Function.identity()));
        List<NoticeFileUploadPresignedUrlCreateResponseItem> items = presignedUrls.stream().map(urlResponse -> {
            NoticeFile noticeFile = filePathToNoticeFile.get(urlResponse.s3Key());
            return NoticeFileUploadPresignedUrlCreateResponseItem.of(noticeFile.getId(), urlResponse.presignedUrl(),
                    urlResponse.fileUrl());
        }).toList();
        return new NoticeFileUploadPresignedUrlCreateResponse(items);
    }
}
