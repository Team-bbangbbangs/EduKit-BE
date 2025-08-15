package com.edukit.core.common.event.ai.dto;

public record DraftGenerationEvent(
        long taskId,
        long recordId,
        String requestPrompt,
        int byteCount,
        int version,
        String draftContent,
        boolean isLastVersion
) {

    public static DraftGenerationEvent of(final long taskId, final long recordId, final String requestPrompt,
                                          final int byteCount, final int version, final String draftContent,
                                          final boolean isLastVersion) {
        return new DraftGenerationEvent(taskId, recordId, requestPrompt, byteCount, version, draftContent, isLastVersion);
    }
}
