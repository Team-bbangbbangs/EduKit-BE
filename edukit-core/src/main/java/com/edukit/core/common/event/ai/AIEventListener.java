package com.edukit.core.common.event.ai;

import com.edukit.core.common.event.ai.dto.DraftGenerationEvent;
import com.edukit.core.common.service.AIService;
import com.edukit.core.common.service.SqsService;
import com.edukit.core.common.service.response.OpenAIVersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@ConditionalOnBean({AIService.class, SqsService.class})
public class AIEventListener {

    private final AIService aiService;
    private final SqsService messageQueueService;

    @Async("aiTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAIResponseGenerateEvent(final AIResponseGenerateEvent generateEvent) {
        Flux<OpenAIVersionResponse> response = aiService.getVersionedStreamingResponse(generateEvent.requestPrompt());

        response.subscribe(version -> {
            DraftGenerationEvent event = DraftGenerationEvent.of(
                    generateEvent.taskId(),
                    generateEvent.recordId(),
                    generateEvent.requestPrompt(),
                    generateEvent.byteCount(),
                    version.versionNumber(),
                    version.content(),
                    version.isLast()
            );
            messageQueueService.sendMessage(event);
        });
    }
}
