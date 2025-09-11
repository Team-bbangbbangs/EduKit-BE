package com.edukit.core.event.ai.dto;

public record DraftGenerationEvent(
        String taskId,
        String requestPrompt,
        int byteCount,
        int version,
        String draftContent,
        String traceId
) {

    public static DraftGenerationEvent of(final String taskId, final String requestPrompt, final int byteCount,
                                          final int version, final String draftContent, final String traceId) {
        return new DraftGenerationEvent(taskId, requestPrompt, byteCount, version, draftContent, traceId);
    }
}
