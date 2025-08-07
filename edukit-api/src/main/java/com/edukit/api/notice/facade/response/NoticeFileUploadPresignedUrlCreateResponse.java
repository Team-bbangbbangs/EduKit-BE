package com.edukit.api.notice.facade.response;

import com.edukit.core.common.service.response.UploadPresignedUrlResponse;
import java.util.List;

public record NoticeFileUploadPresignedUrlCreateResponse(
        List<NoticeFileUploadPresignedUrlCreateResponseItem> images
) {

    public static NoticeFileUploadPresignedUrlCreateResponse of(
            final List<NoticeFileUploadPresignedUrlCreateResponseItem> images
    ) {
        return new NoticeFileUploadPresignedUrlCreateResponse(images);
    }

    public record NoticeFileUploadPresignedUrlCreateResponseItem(
            String uploadPresignedUrl,      // 업로드용 주소
            String tmpFileUrl,              // https://dev-cdn.edukit.co.kr/tmp/20250725_223935_ea5f18be.jpg
            String fileUrl,                 // https://dev-cdn.edukit.co.kr/notices/20250725_223935_ea5f18be.jpg
            String fileKey                  // notices/20250725_223935_ea5f18be.jpg

    ) {
        public static NoticeFileUploadPresignedUrlCreateResponseItem of(
                final UploadPresignedUrlResponse response,
                final String noticeFileDirectory
        ) {
            String fileKey = getFileKey(noticeFileDirectory, response.fileName());
            String fileUrl = getNoticeFileUrl(response.baseUrl(), fileKey);
            return new NoticeFileUploadPresignedUrlCreateResponseItem(
                    response.presignedUrl(),
                    response.fileUrl(),
                    fileUrl,
                    fileKey
            );
        }

        private static String getNoticeFileUrl(final String baseUrl, final String fileKey) {
            return baseUrl + "/" + fileKey;
        }

        private static String getFileKey(final String noticeFileDirectory, final String fileName) {
            return noticeFileDirectory + "/" + fileName;
        }
    }
}
