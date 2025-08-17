package com.edukit.core.common.event.ai;

import com.edukit.common.exception.ExternalException;
import com.edukit.core.common.event.ai.dto.DraftGenerationEvent;
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
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean({AIService.class, SqsService.class})
public class AIEventListener {

    private final AIService aiService;
    private final SqsService messageQueueService;

    @Async("aiTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAIResponseGenerateEvent(final AIResponseGenerateEvent generateEvent) {
        log.info("AI 생기부 생성 시작 taskId: {}, recordId: {}", generateEvent.taskId(), generateEvent.recordId());
        Flux<OpenAIVersionResponse> response = aiService.getVersionedStreamingResponse(generateEvent.requestPrompt());

        response
                .publishOn(Schedulers.boundedElastic())
                .subscribe(
                        version -> {
                            DraftGenerationEvent event = DraftGenerationEvent.of(
                                    generateEvent.taskId(),
                                    generateEvent.recordId(),
                                    generateEvent.userPrompt(),
                                    generateEvent.byteCount(),
                                    version.versionNumber(),
                                    version.content(),
                                    version.isLast()
                            );
                            log.info("Task ID: {} VERSION {} 생성 완료! SQS 전송 시작", generateEvent.taskId(),
                                    version.versionNumber());
                            try {
                                messageQueueService.sendMessage(event);
                            } catch (ExternalException e) {
                                log.error("SQS 메시지 전송 실패 - taskId: {}, recordId: {}, error: {}",
                                        generateEvent.taskId(), generateEvent.recordId(), e.getMessage());
                            }
                        },
                        error -> {
                            log.error("AI 응답 생성 중 오류 발생 - taskId: {}, recordId: {}",
                                    generateEvent.taskId(), generateEvent.recordId(), error);
                        },
                        () -> {
                            log.info("AI 응답 생성 완료 - taskId: {}, recordId: {}",
                                    generateEvent.taskId(), generateEvent.recordId());
                        }
                );
    }
}
