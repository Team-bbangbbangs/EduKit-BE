package com.edukit.api.studentrecord.facade;

import com.edukit.core.common.service.AIService;
import com.edukit.core.common.service.response.OpenAIVersionResponse;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.api.studentrecord.facade.response.StudentRecordCreateResponse;
import com.edukit.api.studentrecord.facade.response.StudentRecordTaskResponse;
import com.edukit.core.studentrecord.service.StudentRecordService;
import com.edukit.core.studentrecord.util.AIPromptGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(AIService.class)
public class StudentRecordAIFacade {

    private final StudentRecordService studentRecordService;
    private final AIService aiService;

    @Transactional
    public StudentRecordTaskResponse getStreamingPrompt(final long memberId, final long recordId, final int byteCount,
                                                        final String userPrompt) {
        StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);

        String requestPrompt = AIPromptGenerator.createStreamingPrompt(studentRecord.getStudentRecordType(), byteCount,
                userPrompt);
        long taskId = studentRecordService.createAITask(studentRecord, userPrompt);
        return StudentRecordTaskResponse.of(taskId, requestPrompt);
    }

    public Flux<StudentRecordCreateResponse> generateAIStudentRecordStream(final String prompt) {
        return aiService.getVersionedStreamingResponse(prompt).map(this::mapToStudentRecordCreateResponse);
    }

    private StudentRecordCreateResponse mapToStudentRecordCreateResponse(final OpenAIVersionResponse response) {
        return StudentRecordCreateResponse.of(response.versionNumber(), response.content(), response.isLast(),
                response.isFallback());
    }

    /* v1.0.0
    @Transactional
    public StudentRecordTaskResponse getPrompt(final long memberId, final long recordId, final int byteCount,
                                               final String userPrompt) {
        Member member = memberService.getMemberById(memberId);
        StudentRecord studentRecord = studentRecordService.getRecordDetail(member.getId(), recordId);

        String requestPrompt = AIPromptGenerator.createPrompt(studentRecord.getStudentRecordType(), byteCount,
                userPrompt);
        long taskId = studentRecordService.createAITask(studentRecord, userPrompt);
        return StudentRecordTaskResponse.of(taskId, requestPrompt);
    }

    public StudentRecordCreateResponse generateAIStudentRecord(final String prompt) {
        OpenAIResponse openAIResponse = openAIService.getMultipleChatResponses(prompt);
        return StudentRecordCreateResponse.of(openAIResponse.description1(), openAIResponse.description2(),
                openAIResponse.description3());
    }
     */
}
