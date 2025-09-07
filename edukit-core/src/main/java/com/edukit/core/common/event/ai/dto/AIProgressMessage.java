package com.edukit.core.common.event.ai.dto;

import com.edukit.core.studentrecord.service.enums.AITaskStatus;

public record AIProgressMessage(
        String taskId,
        AITaskStatus status
) {
    public static AIProgressMessage of(final String taskId, final AITaskStatus status) {
        return new AIProgressMessage(taskId, status);
    }

    public static AIProgressMessage generationStarted(final String taskId) {
        return new AIProgressMessage(taskId, AITaskStatus.PHASE1_STARTED);
    }

    public static AIProgressMessage generationCompleted(final String taskId) {
        return new AIProgressMessage(taskId, AITaskStatus.PHASE1_COMPLETED);
    }
}
