package com.edukit.notice.facade.response;

import com.edukit.core.common.service.response.UploadPresignedUrlResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record NoticeFileUploadPresignedUrlCreateResponse(
        @Schema(description = "업로드용 Presigned URL 목록")
        List<NoticeFileUploadPresignedUrlCreateResponseItem> images
) {

    public static NoticeFileUploadPresignedUrlCreateResponse of(
            final List<NoticeFileUploadPresignedUrlCreateResponseItem> images
    ) {
        return new NoticeFileUploadPresignedUrlCreateResponse(images);
    }

    public record NoticeFileUploadPresignedUrlCreateResponseItem(
            @Schema(description = "파일 업로드용 Presigned URL", example = "https://s3.amazonaws.com/bucket/key?signature=...")
            String uploadPresignedUrl,      // 업로드용 주소
            
            @Schema(description = "임시 파일 URL", example = "https://dev-cdn.edukit.co.kr/tmp/20250725_223935_ea5f18be.jpg")
            String tmpFileUrl,              // https://dev-cdn.edukit.co.kr/tmp/20250725_223935_ea5f18be.jpg
            
            @Schema(description = "최종 파일 URL", example = "https://dev-cdn.edukit.co.kr/notices/20250725_223935_ea5f18be.jpg")
            String fileUrl,                 // https://dev-cdn.edukit.co.kr/notices/20250725_223935_ea5f18be.jpg
            
            @Schema(description = "파일 키", example = "notices/20250725_223935_ea5f18be.jpg")
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
