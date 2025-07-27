package com.edukit.core.studentrecord.facade.response;

public record StudentRecordCreateResponse(
        int versionNumber,
        String content,
        boolean isLast
) {
    public static StudentRecordCreateResponse of(final int versionNumber, final String content, final boolean isLast) {
        return new StudentRecordCreateResponse(versionNumber, content, isLast);
    }
}
