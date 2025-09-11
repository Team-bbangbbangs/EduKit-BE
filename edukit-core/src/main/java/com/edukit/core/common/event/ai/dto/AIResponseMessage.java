package com.edukit.core.common.event.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIResponseMessage(
        @JsonProperty("task_id")
        String taskId,
        @JsonProperty("final_content")
        String content,
        @JsonProperty("version")
        Integer version,
        @JsonProperty("status")
        String status
) {
}
