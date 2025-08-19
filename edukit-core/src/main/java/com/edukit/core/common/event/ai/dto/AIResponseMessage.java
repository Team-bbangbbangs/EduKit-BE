package com.edukit.core.common.event.ai.dto;

public record AIResponseMessage(
        long taskId,
        String data,
        int version
) {
}
