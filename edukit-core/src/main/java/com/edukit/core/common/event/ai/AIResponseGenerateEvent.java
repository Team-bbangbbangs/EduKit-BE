package com.edukit.core.common.event.ai;

public interface AIResponseGenerateEvent {

    long taskId();

    String userPrompt();

    String requestPrompt();

    int byteCount();

    long recordId();
}
