package com.edukit.core.common.event.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIProgressMessage(
        @JsonProperty("task_id")
        String taskId,
        @JsonProperty("message")
        String message
) {
    public static AIProgressMessage of(final String taskId, final String message) {
        return new AIProgressMessage(taskId, message);
    }

    public static AIProgressMessage generationStarted(final String taskId) {
        return new AIProgressMessage(taskId, "3가지 버전 생성 중");
    }

    public static AIProgressMessage generationCompleted(final String taskId) {
        return new AIProgressMessage(taskId, "3가지 버전 생성 완료");
    }
}
