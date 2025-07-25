package com.edukit.external.s3.dto.response;

public record PresignedUrlCreateResponse(
        String presignedUrl,
        String fileUrl,
        String filePath
) {
    public static PresignedUrlCreateResponse of(final String presignedUrl, final String fileUrl, final String filePath) {
        return new PresignedUrlCreateResponse(presignedUrl, fileUrl, filePath);
    }
}
