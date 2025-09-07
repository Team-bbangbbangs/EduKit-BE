package com.edukit.core.common.event.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIProgressMessage(
        @JsonProperty("task_id")
        Long taskId,
        @JsonProperty("status")
        String status,
        @JsonProperty("message")
        String message
) {
    public static AIProgressMessage of(final Long taskId, final String status, final String message) {
        return new AIProgressMessage(taskId, status, message);
    }
    
    public static AIProgressMessage generationStarted(final Long taskId) {
        return new AIProgressMessage(taskId, "GENERATION_STARTED", "3가지 버전 생성 중");
    }
    
    public static AIProgressMessage generationCompleted(final Long taskId) {
        return new AIProgressMessage(taskId, "GENERATION_COMPLETED", "3가지 버전 생성 완료");
    }
}
