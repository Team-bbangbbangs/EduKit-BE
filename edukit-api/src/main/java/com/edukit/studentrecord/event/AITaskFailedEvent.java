package com.edukit.studentrecord.event;

public record AITaskFailedEvent(
        String taskId,
        String errorType
) {

    public static AITaskFailedEvent of(final String taskId, final String errorType) {
        return new AITaskFailedEvent(taskId, errorType);
    }
}
