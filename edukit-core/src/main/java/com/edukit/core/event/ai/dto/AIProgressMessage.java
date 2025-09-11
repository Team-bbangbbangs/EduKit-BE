package com.edukit.core.event.ai.dto;

import com.edukit.core.studentrecord.service.enums.AITaskStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AIProgressMessage(
        @JsonProperty("task_id")
        String taskId,
        @JsonProperty("version")
        Integer version,
        @JsonProperty("status")
        String status
) {
    public static AIProgressMessage of(final String taskId, final int version, final String status) {
        return new AIProgressMessage(taskId, version, status);
    }

    public static AIProgressMessage phaseOneFinished(final String taskId, final int version) {
        return AIProgressMessage.of(taskId, version, AITaskStatus.PHASE1_COMPLETED.getStatus());
    }
}
