package com.edukit.core.common.event.ai;

public interface AIResponseGenerateEvent {

    long taskId();

    String requestPrompt();

    int byteCount();

    long recordId();
}
