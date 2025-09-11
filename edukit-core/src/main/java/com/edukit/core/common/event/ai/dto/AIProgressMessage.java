package com.edukit.core.common.event.ai.dto;

import com.edukit.core.studentrecord.service.enums.AITaskStatus;

public record AIProgressMessage(
        String taskId,
        String status
) implements AIResponse {
    public static AIProgressMessage of(final String taskId, final String status) {
        return new AIProgressMessage(taskId, status);
    }

    public static AIProgressMessage generationStarted(final String taskId) {
        return AIProgressMessage.of(taskId, AITaskStatus.PHASE1_STARTED.getStatus());
    }
}
