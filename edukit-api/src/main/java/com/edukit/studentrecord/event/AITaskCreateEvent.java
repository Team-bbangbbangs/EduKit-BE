package com.edukit.studentrecord.event;

import com.edukit.core.common.event.ai.AIResponseGenerateEvent;

public record AITaskCreateEvent(
        long taskId,
        String requestPrompt,
        int byteCount,
        long recordId

) implements AIResponseGenerateEvent {

    public static AITaskCreateEvent of(final long taskId, final String requestPrompt, final int byteCount,
                                       final long recordId) {
        return new AITaskCreateEvent(taskId, requestPrompt, byteCount, recordId);
    }
}
