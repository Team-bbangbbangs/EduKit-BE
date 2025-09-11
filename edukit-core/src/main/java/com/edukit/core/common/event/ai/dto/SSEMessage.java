package com.edukit.core.common.event.ai.dto;

public record SSEMessage(
        String taskId,
        String type,
        Object data
) {
    public static SSEMessage progress(final String taskId, final String message, final int version) {
        return new SSEMessage(taskId, "PROGRESS", new ProgressData(message, version));
    }

    public static SSEMessage response(final String taskId, final String finalContent, final Integer version) {
        return new SSEMessage(taskId, "RESPONSE", new ResponseData(finalContent, version));
    }

    public record ProgressData(
            String message,
            int version
    ) {
    }

    public record ResponseData(
            String finalContent,
            Integer version
    ) {
    }
}
