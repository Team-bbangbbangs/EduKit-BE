package com.edukit.core.common.event.ai;

import com.edukit.core.common.service.AIService;
import com.edukit.core.common.service.SqsService;
import com.edukit.core.common.service.response.OpenAIVersionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean({AIService.class, SqsService.class})
public class AIEventListener {

    private final AIService aiService;
    private final SqsService messageQueueService;

    @Async("aiTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAIResponseGenerateEvent(final AIResponseGenerateEvent event) {
        Flux<OpenAIVersionResponse> response = aiService.getVersionedStreamingResponse(event.requestPrompt());
        response
                .doOnNext(messageQueueService::sendMessage)
                .doOnError(error -> {
                    log.error("Error processing AI response: {}", error.getMessage());
                })
                .subscribe();
    }
}
