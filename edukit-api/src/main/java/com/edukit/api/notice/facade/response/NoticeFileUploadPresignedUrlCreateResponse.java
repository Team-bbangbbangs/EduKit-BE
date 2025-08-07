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
            String uploadPresignedUrl,      //업로드용 주소
            String tmpImageUrl,             //임시 저장소 주소
            String imageUrl                 //저장소 주소
    ) {
        public static NoticeFileUploadPresignedUrlCreateResponseItem of(
                UploadPresignedUrlResponse presignedUrlResponse,
                String imageUrl
        ) {
            return new NoticeFileUploadPresignedUrlCreateResponseItem(presignedUrlResponse.presignedUrl(),
                    presignedUrlResponse.fileUrl(), imageUrl);
        }
    }
}
