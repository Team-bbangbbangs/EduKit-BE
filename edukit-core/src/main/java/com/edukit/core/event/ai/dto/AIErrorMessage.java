package com.edukit.core.event.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIErrorMessage(
        @JsonProperty("task_id")
        String taskId,
        @JsonProperty("status")
        String status,
        @JsonProperty("error_type")
        String errorType,
        @JsonProperty("error_message")
        String errorMessage,
        @JsonProperty("retryable")
        Boolean retryable
) {
}
