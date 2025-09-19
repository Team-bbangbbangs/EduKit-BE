package com.edukit.core.studentrecord.service;

import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class StudentRecordMetricsService {

    private final MeterRegistry meterRegistry;

    private static final String COMPLETION_METRIC = "student_record_completion_total";
    private static final String AI_GENERATION_REQUEST_METRIC = "student_record_ai_generation_requests_total";
    private static final String AI_FIRST_GENERATION_METRIC = "student_record_ai_first_generation_total";
    private static final String AI_REGENERATION_METRIC = "student_record_ai_regeneration_total";

    public void recordCompletion(final StudentRecordType type, final String description) {
        if (isCompleted(type, description)) {
            Counter.builder(COMPLETION_METRIC)
                    .description("Total number of completed student records")
                    .tags(Tags.of("type", type.name(), "action", "completion"))
                    .register(meterRegistry)
                    .increment();
        }
    }

    public void recordAIGenerationRequest(final StudentRecordType type) {
        Counter.builder(AI_GENERATION_REQUEST_METRIC)
                .description("Total number of AI generation requests for student records")
                .tags(Tags.of("type", type.name(), "action", "ai_generation"))
                .register(meterRegistry)
                .increment();
    }

    public void recordFirstGeneration(final StudentRecordType type) {
        Counter.builder(AI_FIRST_GENERATION_METRIC)
                .description("Total number of first AI generation requests")
                .tags(Tags.of("type", type.name(), "action", "first_generation"))
                .register(meterRegistry)
                .increment();
    }

    public void recordRegeneration(final StudentRecordType type) {
        Counter.builder(AI_REGENERATION_METRIC)
                .description("Total number of AI regeneration requests")
                .tags(Tags.of("type", type.name(), "action", "regeneration"))
                .register(meterRegistry)
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
