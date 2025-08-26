package com.edukit.studentrecord.controller.request;

public record StudentRecordDetailResponse(
        String description
) {
    public static StudentRecordDetailResponse of(final String description) {
        return new StudentRecordDetailResponse(description);
    }
}
