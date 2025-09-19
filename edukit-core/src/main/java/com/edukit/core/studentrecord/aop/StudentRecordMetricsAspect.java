package com.edukit.core.studentrecord.aop;

import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.core.studentrecord.service.GenerationTrackingService;
import com.edukit.core.studentrecord.service.StudentRecordMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class StudentRecordMetricsAspect {

    private final StudentRecordMetricsService metricsService;
    private final GenerationTrackingService generationTrackingService;

    @AfterReturning("@annotation(com.edukit.common.annotation.StudentRecordMetrics)")
    public void collectCompletionMetrics(final JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length >= 4) {
            long recordId = (Long) args[1];
            StudentRecordType recordType = (StudentRecordType) args[2];
            String description = (String) args[3];

            try {
                // 트랜잭션 커밋 후에만 메트릭 수집 및 정리 작업 수행
                if (TransactionSynchronizationManager.isSynchronizationActive()) {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            try {
                                metricsService.recordCompletion(recordType, description);

                                // 커밋 성공 후 생성 추적 정보 정리 (메모리 절약)
                                generationTrackingService.clearRecord(recordId);

                                log.debug("Completion metrics recorded after transaction commit for recordId: {}", recordId);

                            } catch (Exception e) {
                                log.warn("Error collecting completion metrics after commit for recordId: {}", recordId, e);
                            }
                        }

                        @Override
                        public void afterCompletion(int status) {
                            if (status == STATUS_ROLLED_BACK) {
                                // 롤백 시에도 생성 추적 정보 정리 (메모리 누수 방지)
                                generationTrackingService.clearRecord(recordId);
                                log.debug("Transaction rolled back - cleared generation tracking for recordId: {}", recordId);
                            }
                        }
                    });
                } else {
                    metricsService.recordCompletion(recordType, description);
                    generationTrackingService.clearRecord(recordId);
                    log.warn("No transaction synchronization - recording metrics immediately for recordId: {}", recordId);
                }

            } catch (Exception e) {
                log.warn("Error setting up completion metrics collection", e);
            }
        }
    }

    @Around("@annotation(com.edukit.common.annotation.AIGenerationMetrics)")
    public Object collectAIGenerationMetrics(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        if (args.length >= 3) {
            long recordId = (Long) args[1];
            StudentRecordType recordType = (StudentRecordType) args[2];

            try {
                boolean isFirstGeneration = generationTrackingService.isFirstGeneration(recordId);

                // 전체 AI 생성 요청 카운트
                metricsService.recordAIGenerationRequest(recordType);

                // 첫 생성 vs 재생성 구분 메트릭
                if (isFirstGeneration) {
                    metricsService.recordFirstGeneration(recordType);
                    log.debug("First generation request for recordId: {}", recordId);
                } else {
                    metricsService.recordRegeneration(recordType);
                    log.debug("Regeneration request for recordId: {}", recordId);
                }

            } catch (Exception e) {
                // 메트릭 수집 실패는 로그만 남기고 비즈니스 로직은 계속 진행
                log.warn("Error collecting AI generation metrics for recordId: {}", recordId, e);
            }
        }

        // 비즈니스 로직은 반드시 1회만 실행, 예외는 그대로 전파
        return joinPoint.proceed();
    }
}
