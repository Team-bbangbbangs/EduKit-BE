package com.edukit.notice.facade.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record NoticeResponse(
        @Schema(description = "공지사항 ID", example = "1")
        long noticeId,
        
        @Schema(description = "공지사항 카테고리", example = "ANNOUNCEMENT")
        String category,
        
        @Schema(description = "공지사항 제목", example = "신규 기능 업데이트 안내")
        String title,
        
        @Schema(description = "생성 날짜", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt
) {
    public static NoticeResponse of(final long noticeId,
                                    final String category,
                                    final String title,
                                    final LocalDateTime createdAt) {
        return new NoticeResponse(
                noticeId,
                category,
                title,
                createdAt
        );
    }
}
