package com.edukit.studentrecord.facade;

import com.edukit.core.common.service.AIService;
import com.edukit.core.common.service.response.OpenAIVersionResponse;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.service.StudentRecordService;
import com.edukit.core.studentrecord.util.AIPromptGenerator;
import com.edukit.studentrecord.event.AITaskCreateEvent;
import com.edukit.studentrecord.facade.response.StudentRecordCreateResponse;
import com.edukit.studentrecord.facade.response.StudentRecordTaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(AIService.class)
public class StudentRecordAIFacade {

    private final AIService aiService;
    private final StudentRecordService studentRecordService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public StudentRecordTaskResponse getStreamingPrompt(final long memberId, final long recordId, final int byteCount,
                                                        final String userPrompt) {
        StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);

        String requestPrompt = AIPromptGenerator.createStreamingPrompt(studentRecord.getStudentRecordType(), byteCount, userPrompt);
        long taskId = studentRecordService.createAITask(studentRecord, userPrompt);

        eventPublisher.publishEvent(AITaskCreateEvent.of(taskId, requestPrompt));
        return StudentRecordTaskResponse.of(taskId);
    }

    public Flux<StudentRecordCreateResponse> generateAIStudentRecordStream(final String prompt) {
        return aiService.getVersionedStreamingResponse(prompt).map(this::mapToStudentRecordCreateResponse);
    }

    private StudentRecordCreateResponse mapToStudentRecordCreateResponse(final OpenAIVersionResponse response) {
        return StudentRecordCreateResponse.of(response.versionNumber(), response.content(), response.isLast(),
                response.isFallback());
    }
}
