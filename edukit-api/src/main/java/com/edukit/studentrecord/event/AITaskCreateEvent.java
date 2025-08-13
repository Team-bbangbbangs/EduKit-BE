package com.edukit.studentrecord.event;

import com.edukit.core.common.event.ai.AIResponseGenerateEvent;

public record AITaskCreateEvent(
        long taskId,
        String requestPrompt

) implements AIResponseGenerateEvent {

    public static AITaskCreateEvent of(final long taskId, final String requestPrompt) {
        return new AITaskCreateEvent(taskId, requestPrompt);
    }
}
