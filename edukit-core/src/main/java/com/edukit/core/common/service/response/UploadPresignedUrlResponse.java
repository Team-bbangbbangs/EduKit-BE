package com.edukit.core.common.service.response;

public record UploadPresignedUrlResponse(
        String presignedUrl,
        String fileUrl,         // https://dev-cdn.edukit.co.kr/tmp/20250725_223935_ea5f18be.jpg
        String baseUrl,         // https://dev-cdn.edukit.co.kr
        String fileName         // 20250725_223935_ea5f18be.jpg
) {
    public static UploadPresignedUrlResponse of(final String presignedUrl, final String fileUrl, final String baseUrl, final String fileName) {
        return new UploadPresignedUrlResponse(presignedUrl, fileUrl, baseUrl, fileName);
    }
}
