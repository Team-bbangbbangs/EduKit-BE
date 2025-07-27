package com.edukit.core.studentrecord.facade.response;

public record StudentRecordCreateResponse(
        String description1,
        String description2,
        String description3
) {
    public static StudentRecordCreateResponse of(final String description1, final String description2,
                                                 final String description3) {
        return new StudentRecordCreateResponse(description1, description2, description3);
    }
}
