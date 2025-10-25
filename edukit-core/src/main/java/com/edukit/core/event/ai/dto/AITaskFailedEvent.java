package com.edukit.core.event.ai.dto;

public record AITaskFailedEvent(
        String taskId,
        String errorType
) {

    public static AITaskFailedEvent of(final String taskId, final String errorType) {
        return new AITaskFailedEvent(taskId, errorType);
    }

    public static AITaskFailedEvent fromErrorMessage(final AIErrorMessage errorMessage) {
        return AITaskFailedEvent.of(
                errorMessage.taskId(),
                errorMessage.errorType()
        );
    }
}
