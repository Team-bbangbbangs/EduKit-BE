package com.edukit.core.event.ai;

import com.edukit.core.common.service.SqsService;
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

    private final SqsService messageQueueService;

    @Async("aiTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAIResponseGenerateEvent(final AIResponseGenerateEvent generateEvent) {
        String taskId = String.valueOf(generateEvent.taskId());

        AITask task = new AITask(
                generateEvent.userPrompt(),
                generateEvent.requestPrompt(),
                generateEvent.byteCount()
        );
        log.info("AI 작업 SQS로 전송: {}", taskId);
        messageQueueService.sendMessage(task, taskId);
    }

    private record AITask(
            String userPrompt,
            String requestPrompt,
            int byteCount

    ) {
    }
}
