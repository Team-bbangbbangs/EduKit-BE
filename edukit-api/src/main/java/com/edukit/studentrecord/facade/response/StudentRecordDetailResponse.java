package com.edukit.studentrecord.facade.response;

public record StudentRecordDetailResponse(
        String description
) {
    public static StudentRecordDetailResponse of(final String description) {
        return new StudentRecordDetailResponse(description);
    }
}
