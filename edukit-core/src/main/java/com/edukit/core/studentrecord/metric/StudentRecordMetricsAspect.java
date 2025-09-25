package com.edukit.core.studentrecord.metric;

import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.core.studentrecord.service.StudentRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class StudentRecordMetricsAspect {

    private final StudentRecordMetricsCounter metricsService;
    private final RecordGenerationTracker recordGenerationTracker;
    private final StudentRecordService studentRecordService;

    @AfterReturning(pointcut = "@annotation(com.edukit.common.annotation.StudentRecordMetrics)")
    public void collectCompletionMetrics(final JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length == 3) {
            long memberId = (Long) args[0];
            long recordId = (Long) args[1];
            String description = (String) args[2];

            StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);
            StudentRecordType recordType = studentRecord.getStudentRecordType();

            try {
                metricsService.recordCompletion(recordType, description);
            } catch (Exception e) {
                // 메트릭 수집 실패는 로그만 남기고 비즈니스 로직은 계속 진행
                log.warn("Error collecting completion metrics for recordType: {}", recordType, e);
            }
        }
    }

    @Around("@annotation(com.edukit.common.annotation.AIGenerationMetrics)")
    public Object collectAIGenerationMetrics(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        if (args.length == 4) {
            long memberId = (Long) args[0];
            long recordId = (Long) args[1];

            StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);
            StudentRecordType recordType = studentRecord.getStudentRecordType();

            try {
                boolean isFirstGeneration = recordGenerationTracker.isFirstGeneration(recordId);

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
