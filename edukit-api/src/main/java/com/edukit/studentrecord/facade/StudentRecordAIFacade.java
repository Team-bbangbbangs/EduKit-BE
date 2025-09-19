package com.edukit.studentrecord.facade;

import com.edukit.common.annotation.AIGenerationMetrics;
import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.core.studentrecord.service.AITaskService;
import com.edukit.core.studentrecord.service.SSEChannelManager;
import com.edukit.core.studentrecord.service.StudentRecordService;
import com.edukit.core.studentrecord.util.AIPromptGenerator;
import com.edukit.studentrecord.event.AITaskCreateEvent;
import com.edukit.studentrecord.facade.response.StudentRecordTaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class StudentRecordAIFacade {

    private final MemberService memberService;
    private final StudentRecordService studentRecordService;
    private final AITaskService aiTaskService;
    private final SSEChannelManager sseChannelManager;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @AIGenerationMetrics
    public StudentRecordTaskResponse createTaskId(final long memberId, final long recordId,
                                                  final StudentRecordType recordType, final int byteCount,
                                                  final String userPrompt) {
        Member member = memberService.getMemberById(memberId);

        String requestPrompt = AIPromptGenerator.createStreamingPrompt(recordType, byteCount, userPrompt);
        StudentRecordAITask task = aiTaskService.createAITask(member, userPrompt);

        eventPublisher.publishEvent(AITaskCreateEvent.of(task, userPrompt, requestPrompt, byteCount));
        return StudentRecordTaskResponse.of(String.valueOf(task.getId()));
    }

    // 컨트롤러 호환성을 위한 오버로드 메서드
    @Transactional
    public StudentRecordTaskResponse createTaskId(final long memberId, final long recordId,
                                                  final int byteCount, final String userPrompt) {
        StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);
        // 타입을 조회한 후 메트릭 포함 메서드 호출
        return createTaskId(memberId, recordId, studentRecord.getStudentRecordType(), byteCount, userPrompt);
    }

    public SseEmitter createChannel(final long memberId, final String taskId) {
        aiTaskService.validateUserTask(memberId, taskId);

        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        sseChannelManager.registerTaskChannel(taskId, emitter);
        return emitter;
    }

    public void closeChannel(final String taskId) {
        sseChannelManager.removeChannel(taskId);
    }
}
