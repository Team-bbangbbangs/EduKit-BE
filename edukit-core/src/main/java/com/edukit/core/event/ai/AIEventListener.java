package com.edukit.core.event.ai;

import com.edukit.core.common.service.SqsService;
import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import com.edukit.core.studentrecord.service.AITaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(SqsService.class)
public class AIEventListener {

    private final AITaskService aiTaskService;
    private final SqsService messageQueueService;

    @Async("aiTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAIResponseGenerateEvent(final AIResponseGenerateEvent generateEvent) {
        StudentRecordAITask task = generateEvent.task();
        String taskId = String.valueOf(task.getId());

        log.info("AI 작업 SQS로 전송: {}", taskId);
        aiTaskService.startTask(task);
        messageQueueService.sendMessage(task.getPrompt(), taskId);
    }
}
