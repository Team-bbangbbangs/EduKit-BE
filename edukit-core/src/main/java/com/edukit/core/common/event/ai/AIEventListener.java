package com.edukit.core.common.event.ai;

import com.edukit.common.exception.ExternalException;
import com.edukit.core.common.event.ai.dto.DraftGenerationEvent;
import com.edukit.core.common.service.AIService;
import com.edukit.core.common.service.SqsService;
import com.edukit.core.common.service.response.OpenAIVersionResponse;
import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import com.edukit.core.studentrecord.service.AITaskService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
    private final AITaskService aiTaskService;
    private final SqsService messageQueueService;

    @Async("aiTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAIResponseGenerateEvent(final AIResponseGenerateEvent generateEvent) {
        StudentRecordAITask task = generateEvent.task();
        long taskId = task.getId();

        log.info("AI 생기부 생성 시작 taskId: {}", taskId);

        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        Flux<OpenAIVersionResponse> response = aiService.getVersionedStreamingResponse(generateEvent.requestPrompt());

        response
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(version -> {
                    if (mdcContextMap != null) {
                        MDC.setContextMap(mdcContextMap);
                    }
                })
                .doFinally(signalType -> {
                    MDC.clear();
                })
                .subscribe(
                        version -> {
                            try {
                                String traceId = MDC.get("traceId");
                                DraftGenerationEvent event = DraftGenerationEvent.of(
                                        taskId,
                                        generateEvent.userPrompt(),
                                        generateEvent.byteCount(),
                                        version.versionNumber(),
                                        version.content(),
                                        traceId
                                );
                                log.info("Task ID: {} VERSION {} 생성 완료! SQS 전송 시작", taskId, version.versionNumber());
                                messageQueueService.sendMessage(event);
                            } catch (ExternalException e) {
                                log.error("SQS 메시지 전송 실패 - taskId: {}, error: {}", taskId, e.getMessage());
                            } finally {
                                MDC.clear();
                            }
                        },
                        error -> {
                            try {
                                if (mdcContextMap != null) {
                                    MDC.setContextMap(mdcContextMap);
                                }
                                log.error("AI 응답 생성 중 오류 발생 - taskId: {}", taskId, error);
                            } finally {
                                MDC.clear();
                            }
                        },
                        () -> {
                            try {
                                if (mdcContextMap != null) {
                                    MDC.setContextMap(mdcContextMap);
                                }
                                log.info("AI 응답 생성 완료 - taskId: {}", taskId);
                            } finally {
                                MDC.clear();
                            }
                        }
                );
    }
}
