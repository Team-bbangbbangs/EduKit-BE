package com.edukit.studentrecord.event;

import com.edukit.core.event.ai.AIResponseGenerateEvent;

public record AITaskCreateEvent(
        String taskId,
        String userPrompt,
        String requestPrompt,
        int byteCount
) implements AIResponseGenerateEvent {

    public static AITaskCreateEvent of(final String taskId, final String userPrompt, final String requestPrompt,
                                       final int byteCount) {
        return new AITaskCreateEvent(taskId, userPrompt, requestPrompt, byteCount);
    }
}
