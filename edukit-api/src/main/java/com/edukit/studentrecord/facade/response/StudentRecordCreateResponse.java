package com.edukit.studentrecord.facade.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record StudentRecordCreateResponse(
        @Schema(description = "생활기록부 버전 번호", example = "1")
        int versionNumber,
        
        @Schema(description = "생활기록부 내용", example = "수학 수업에 적극적으로 참여하며, 문제 해결 능력이 뛰어나다.")
        String content,
        
        @Schema(description = "마지막 버전 여부", example = "true")
        boolean isLast,
        
        @Schema(description = "폴백 사용 여부", example = "false")
        boolean isFallback
) {
    public static StudentRecordCreateResponse of(final int versionNumber, final String content, final boolean isLast) {
        return new StudentRecordCreateResponse(versionNumber, content, isLast, false);
    }

    public static StudentRecordCreateResponse of(final int versionNumber, final String content, final boolean isLast, final boolean isFallback) {
        return new StudentRecordCreateResponse(versionNumber, content, isLast, isFallback);
    }
}
