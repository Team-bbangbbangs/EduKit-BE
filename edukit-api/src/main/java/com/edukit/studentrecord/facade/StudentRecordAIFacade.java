package com.edukit.studentrecord.facade;

import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
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

    private final StudentRecordService studentRecordService;
    private final AITaskService aiTaskService;
    private final SSEChannelManager sseChannelManager;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public StudentRecordTaskResponse createTaskId(final long memberId, final long recordId, final int byteCount,
                                                  final String userPrompt) {
        StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);

        String requestPrompt = AIPromptGenerator.createStreamingPrompt(studentRecord.getStudentRecordType(), byteCount,
                userPrompt);
        StudentRecordAITask task = aiTaskService.createAITask(userPrompt);

        eventPublisher.publishEvent(AITaskCreateEvent.of(task, userPrompt, requestPrompt, byteCount));
        return StudentRecordTaskResponse.of(task.getId());
    }

    public SseEmitter createChannel(final long taskId) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L);
        sseChannelManager.registerTaskChannel(String.valueOf(taskId), emitter);
        return emitter;
    }

    public void closeChannel(final long taskId) {
        sseChannelManager.removeChannel(String.valueOf(taskId));
    }
}
