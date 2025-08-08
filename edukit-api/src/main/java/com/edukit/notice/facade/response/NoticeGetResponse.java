package com.edukit.notice.facade.response;

import java.time.LocalDateTime;
import java.util.List;

public record NoticeGetResponse(
        long noticeId,
        String category,
        String title,
        String content,
        LocalDateTime createdAt,
        List<Long> noticeFileIds
) {
    public static NoticeGetResponse of(final long noticeId,
                                       final String category,
                                       final String title,
                                       final String content,
                                       final LocalDateTime createdAt,
                                       final List<Long> noticeFileIds) {
        return new NoticeGetResponse(
                noticeId,
                category,
                title,
                content,
                createdAt,
                noticeFileIds
        );
    }
}
