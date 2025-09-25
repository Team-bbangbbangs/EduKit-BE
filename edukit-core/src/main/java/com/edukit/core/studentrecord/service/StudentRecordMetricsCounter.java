package com.edukit.core.studentrecord.service;

import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import io.micrometer.core.instrument.MeterRegistry;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentRecordMetricsCounter {

    private final MeterRegistry meterRegistry;

    private static final String COMPLETION_METRIC = "student_record_completion_total";
    private static final String AI_GENERATION_REQUEST_METRIC = "student_record_ai_generation_requests_total";
    private static final String AI_FIRST_GENERATION_METRIC = "student_record_ai_first_generation_total";
    private static final String AI_REGENERATION_METRIC = "student_record_ai_regeneration_total";

    public void recordCompletion(final StudentRecordType type, final String description) {
        if (isCompleted(type, description)) {
            meterRegistry.counter(COMPLETION_METRIC,
                    "type", type.name(), "action", "completion")
                    .increment();
        }
    }

    public void recordAIGenerationRequest(final StudentRecordType type) {
        meterRegistry.counter(AI_GENERATION_REQUEST_METRIC,
                "type", type.name(), "action", "ai_generation")
                .increment();
    }

    public void recordFirstGeneration(final StudentRecordType type) {
        meterRegistry.counter(AI_FIRST_GENERATION_METRIC,
                "type", type.name(), "action", "first_generation")
                .increment();
    }

    public void recordRegeneration(final StudentRecordType type) {
        meterRegistry.counter(AI_REGENERATION_METRIC,
                "type", type.name(), "action", "regeneration")
                .increment();
    }

    private boolean isCompleted(final StudentRecordType type, final String description) {
        if (description == null || description.trim().isEmpty()) {
            return false;
        }

        int minBytes = (type == StudentRecordType.SUBJECT) ? 1000 : 750;
        return description.getBytes(StandardCharsets.UTF_8).length >= minBytes;
    }
}
