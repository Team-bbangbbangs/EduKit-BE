package com.edukit.studentrecord.event;

import com.edukit.core.event.ai.dto.AIErrorMessage;

public record AITaskFailedEvent(
        String taskId,
        String errorType,
        String errorMessage,
        Boolean retryable
) {

    public static AITaskFailedEvent of(final String taskId, final String errorType, final String errorMessage, final Boolean retryable) {
        return new AITaskFailedEvent(taskId, errorType, errorMessage, retryable);
    }

    public static AITaskFailedEvent fromErrorMessage(final AIErrorMessage errorMessage) {
        return AITaskFailedEvent.of(
                errorMessage.taskId(),
                errorMessage.errorType(),
                errorMessage.errorMessage(),
                errorMessage.retryable()
        );
    }
}
