package com.edukit.studentrecord.event;

import com.edukit.core.common.event.ai.AIResponseGenerateEvent;
import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;

public record AITaskCreateEvent(
        StudentRecordAITask task,
        String userPrompt,
        String requestPrompt,
        int byteCount
) implements AIResponseGenerateEvent {

    public static AITaskCreateEvent of(final StudentRecordAITask task, final String userPrompt,
                                       final String requestPrompt, final int byteCount) {
        return new AITaskCreateEvent(task, userPrompt, requestPrompt, byteCount);
    }
}
