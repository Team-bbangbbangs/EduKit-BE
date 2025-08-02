package com.edukit.core.studentrecord.facade.response;

public record StudentRecordCreateResponse(
        int versionNumber,
        String content,
        boolean isLast,
        boolean isFallback
) {
    public static StudentRecordCreateResponse of(final int versionNumber, final String content, final boolean isLast) {
        return new StudentRecordCreateResponse(versionNumber, content, isLast, false);
    }

    public static StudentRecordCreateResponse of(final int versionNumber, final String content, final boolean isLast, final boolean isFallback) {
        return new StudentRecordCreateResponse(versionNumber, content, isLast, isFallback);
    }
}
