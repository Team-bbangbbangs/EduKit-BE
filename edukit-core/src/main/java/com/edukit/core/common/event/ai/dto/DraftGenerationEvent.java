package com.edukit.core.common.event.ai.dto;

public record DraftGenerationEvent(
        long taskId,
        String requestPrompt,
        int byteCount,
        int version,
        String draftContent
) {

    public static DraftGenerationEvent of(final long taskId, final String requestPrompt, final int byteCount,
                                          final int version, final String draftContent) {
        return new DraftGenerationEvent(taskId, requestPrompt, byteCount, version, draftContent);
    }
}
