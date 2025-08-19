package com.edukit.core.common.event.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIResponseMessage(
        @JsonProperty("task_id")
        Long taskId,
        @JsonProperty("final_content")
        String reviewedContent,
        @JsonProperty("version")
        Integer version
) {
}
