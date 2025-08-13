package com.edukit.core.common.event.ai;

import com.edukit.core.common.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(AIService.class)
public class AIEventListener {

    private final AIService aiService;

    @Async("aiTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAIResponseGenerateEvent(final AIResponseGenerateEvent event) {
        // Handle the AI response generation event
    }
}
