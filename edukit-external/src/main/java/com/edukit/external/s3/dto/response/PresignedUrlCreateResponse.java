package com.edukit.external.s3.dto.response;

public record PresignedUrlCreateResponse(
        String presignedUrl,
        String imageUrl
) {
    public static PresignedUrlCreateResponse of(String presignedUrl, String imageUrl) {
        return new PresignedUrlCreateResponse(presignedUrl, imageUrl);
    }
}
