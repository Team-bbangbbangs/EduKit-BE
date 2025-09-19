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

    private static final String API_CALL_METRIC = "student_record_update_api_calls_total";
    private static final String COMPLETION_METRIC = "student_record_completion_total";
    private static final String AI_GENERATION_REQUEST_METRIC = "student_record_ai_generation_requests_total";

    public void recordApiCall(final StudentRecordType type) {
        Counter.builder(API_CALL_METRIC)
                .description("Total number of student record update API calls")
                .tags(Tags.of("type", type.name(), "action", "update"))
                .register(meterRegistry)
                .increment();
    }

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

    private boolean isCompleted(final StudentRecordType type, final String description) {
        if (description == null || description.trim().isEmpty()) {
            return false;
        }

        int minBytes = (type == StudentRecordType.SUBJECT) ? 1000 : 750;
        return description.getBytes(StandardCharsets.UTF_8).length >= minBytes;
    }
}
