package com.edukit.core.event.ai;

import com.edukit.core.common.service.RedisStoreService;
import com.edukit.core.point.service.PointService;
import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import com.edukit.core.studentrecord.service.AITaskService;
import com.edukit.studentrecord.event.AITaskFailedEvent;
import java.time.Duration;
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
@ConditionalOnBean(RedisStoreService.class)
public class AICompensationListener {

    private final PointService pointService;
    private final AITaskService aiTaskService;
    private final RedisStoreService redisStoreService;

    private static final String COMPENSATION_KEY_PREFIX = "compensation:";
    private static final int DEDUCTED_POINTS = 100;
    private static final Duration COMPENSATION_RECORD_TTL = Duration.ofDays(7);

    @Async("aiTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAITaskFailure(final AITaskFailedEvent event) {
        String taskId = event.taskId();

        // 멱등성 보장 - 이미 보상된 경우 스킵
        String compensationKey = COMPENSATION_KEY_PREFIX + taskId;
        if (isAlreadyCompensated(compensationKey)) {
            log.warn("Task {} already compensated, skipping", taskId);
            return;
        }

        try {
            // Task 정보 조회
            StudentRecordAITask task = aiTaskService.getTaskById(Long.valueOf(taskId));

            // Task를 실패로 마킹
            aiTaskService.markTaskAsFailed(Long.valueOf(taskId), event.errorType());

            // 포인트 보상
            pointService.compensatePoints(
                    task.getMember().getId(),
                    DEDUCTED_POINTS,
                    task.getId()
            );

            // 보상 완료 마킹 (멱등성 보장)
            markAsCompensated(compensationKey);

            log.info("Successfully compensated {} points for taskId: {} (errorType: {})",
                    DEDUCTED_POINTS, taskId, event.errorType());

        } catch (Exception e) {
            log.error("Failed to compensate points for taskId: {}", taskId, e);
        }
    }

    private boolean isAlreadyCompensated(final String compensationKey) {
        return redisStoreService.get(compensationKey) != null;
    }

    private void markAsCompensated(final String compensationKey) {
        // 7일간 보관 (감사 목적)
        redisStoreService.store(compensationKey, "COMPENSATED", COMPENSATION_RECORD_TTL);
    }
}
