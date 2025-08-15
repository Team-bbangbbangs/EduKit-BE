package com.edukit.studentrecord.facade.response;

public record StudentRecordTaskResponse(
        long taskId
) {
    public static StudentRecordTaskResponse of(final long taskId) {
        return new StudentRecordTaskResponse(taskId);
    }
}
