package com.edukit.core.common.event.ai.dto;

public record AIResponseMessage(
        Long taskId,
        String reviewedContent,
        Integer version,
        Long recordId
) {
}
