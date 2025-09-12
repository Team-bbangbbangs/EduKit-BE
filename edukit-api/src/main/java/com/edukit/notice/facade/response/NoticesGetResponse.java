package com.edukit.notice.facade.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record NoticesGetResponse(
        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages,
        
        @Schema(description = "공지사항 목록")
        List<NoticeResponse> notices
) {
    public static NoticesGetResponse of(final int totalPages, final List<NoticeResponse> notices) {
        return new NoticesGetResponse(
                totalPages,
                notices
        );
    }
}
