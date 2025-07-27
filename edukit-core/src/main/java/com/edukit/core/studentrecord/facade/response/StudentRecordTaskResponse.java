package com.edukit.core.studentrecord.facade.response;

public record StudentRecordTaskResponse(
        long taskId,
        String inputPrompt
) {
    public static StudentRecordTaskResponse of(final long taskId, final String inputPrompt) {
        return new StudentRecordTaskResponse(taskId, inputPrompt);
    }
}
