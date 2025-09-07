package com.edukit.studentrecord.facade.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record StudentRecordTaskResponse(
        @Schema(description = "생성된 AI 작업 ID", example = "123")
        String taskId
) {
    public static StudentRecordTaskResponse of(final String taskId) {
        return new StudentRecordTaskResponse(taskId);
    }
}
