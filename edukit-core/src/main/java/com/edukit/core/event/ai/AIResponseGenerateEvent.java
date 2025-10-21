package com.edukit.core.event.ai;

public interface AIResponseGenerateEvent {

    String taskId();

    String userPrompt();

    String requestPrompt();

    int byteCount();
}
