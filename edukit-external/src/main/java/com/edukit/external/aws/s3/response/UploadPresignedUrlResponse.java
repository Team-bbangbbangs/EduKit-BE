package com.edukit.external.aws.s3.response;

public record UploadPresignedUrlResponse(
        String presignedUrl,
        String fileUrl,         // https://dev-cdn.edukit.co.kr/notices/20250725_223935_ea5f18be.jpg
        String s3Key            // notices/20250725_223935_ea5f18be.jpg
) {
    public static UploadPresignedUrlResponse of(final String presignedUrl, final String fileUrl, final String s3Key) {
        return new UploadPresignedUrlResponse(presignedUrl, fileUrl, s3Key);
    }
}
