package com.edukit.core.studentrecord.facade;

import com.edukit.core.studentrecord.entity.StudentRecord;
import com.edukit.core.studentrecord.facade.response.StudentRecordCreateResponse;
import com.edukit.core.studentrecord.facade.response.StudentRecordTaskResponse;
import com.edukit.core.studentrecord.service.StudentRecordService;
import com.edukit.core.studentrecord.util.AIPromptGenerator;
import com.edukit.external.ai.OpenAIService;
import com.edukit.external.ai.response.OpenAIVersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StudentRecordAIFacade {

    private final StudentRecordService studentRecordService;
    private final OpenAIService openAIService;

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
        return openAIService.getVersionedStreamingResponse(prompt)
                .map(this::mapToStudentRecordCreateResponse);
    }

    private StudentRecordCreateResponse mapToStudentRecordCreateResponse(final OpenAIVersionResponse response) {
        return StudentRecordCreateResponse.of(response.versionNumber(), response.content(), response.isLast());
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
