package com.edukit.core.event.ai;

import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;

public interface AIResponseGenerateEvent {

    StudentRecordAITask task();

    String userPrompt();

    String requestPrompt();

    int byteCount();
}
