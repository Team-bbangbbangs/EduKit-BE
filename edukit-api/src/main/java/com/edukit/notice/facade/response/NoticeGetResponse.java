package com.edukit.notice.facade.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public record NoticeGetResponse(
        @Schema(description = "공지사항 ID", example = "1")
        long noticeId,
        
        @Schema(description = "공지사항 카테고리", example = "ANNOUNCEMENT")
        String category,
        
        @Schema(description = "공지사항 제목", example = "신규 기능 업데이트 안내")
        String title,
        
        @Schema(description = "공지사항 내용", example = "새로운 AI 생활기록부 생성 기능이 추가되었습니다. 자세한 사용 방법은 첨부 파일을 확인해주세요.")
        String content,
        
        @Schema(description = "생성 날짜", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt,
        
        @Schema(description = "첨부된 파일 key 목록", example = "[\"newfile.jpg\"]")
        List<String> noticeFileKeys
) {
    public static NoticeGetResponse of(final long noticeId,
                                       final String category,
                                       final String title,
                                       final String content,
                                       final LocalDateTime createdAt,
                                       final List<String> noticeFileKeys) {
        return new NoticeGetResponse(
                noticeId,
                category,
                title,
                content,
                createdAt,
                noticeFileKeys
        );
    }
}
