package com.edukit.core.event.ai;

import com.edukit.core.common.service.RedisStoreService;
import com.edukit.core.event.ai.dto.AITaskFailedEvent;
import com.edukit.core.point.service.PointService;
import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import com.edukit.core.studentrecord.service.AITaskService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
    @EventListener
    public void handleAITaskFailure(final AITaskFailedEvent event) {
        String taskId = event.taskId();
        String compensationKey = COMPENSATION_KEY_PREFIX + taskId;

        // 원자적 선점 - Redis SET NX로 중복 보상 방지
        if (!tryClaimCompensation(compensationKey)) {
            log.warn("Task {} already compensated by another instance, skipping", taskId);
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

            log.info("Successfully compensated {} points for taskId: {} (errorType: {})",
                    DEDUCTED_POINTS, taskId, event.errorType());

        } catch (Exception e) {
            log.error("Failed to compensate points for taskId: {}", taskId, e);
            // 보상 실패 시 Redis 키 삭제하여 재시도 가능하게 함
            redisStoreService.delete(compensationKey);
            throw e;
        }
    }

    /**
     * 원자적 선점 시도
     * @return true: 선점 성공 (보상 실행), false: 이미 다른 인스턴스가 선점 (스킵)
     */
    private boolean tryClaimCompensation(final String compensationKey) {
        Boolean claimed = redisStoreService.setIfAbsent(compensationKey, "COMPENSATED", COMPENSATION_RECORD_TTL);
        return Boolean.TRUE.equals(claimed);
    }
}
