package com.edukit.core.notice.facade.response;

public record NoticeImageUploadPresignedUrlCreateResponse(
        String presignedUrl,
        String imageUrl
) {
}
