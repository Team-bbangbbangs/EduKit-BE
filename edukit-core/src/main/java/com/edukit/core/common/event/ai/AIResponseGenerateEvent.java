package com.edukit.core.common.event.ai;

import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;

public interface AIResponseGenerateEvent {

    StudentRecordAITask task();

    String userPrompt();

    String requestPrompt();

    int byteCount();
}
