package com.edukit.core.studentrecord.aop;

import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.service.GenerationTrackingService;
import com.edukit.core.studentrecord.service.StudentRecordMetricsService;
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

    private final StudentRecordMetricsService metricsService;
    private final StudentRecordService studentRecordService;
    private final GenerationTrackingService generationTrackingService;

    @AfterReturning("@annotation(com.edukit.common.annotation.StudentRecordMetrics)")
    public void collectCompletionMetrics(final JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length >= 3) {
            long memberId = (long) args[0];
            long recordId = (long) args[1];
            String description = (String) args[2];

            try {
                StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);

                metricsService.recordCompletion(studentRecord.getStudentRecordType(), description);

                // 저장 완료 후 해당 recordId의 생성 추적 정보 정리 (메모리 절약)
                generationTrackingService.clearRecord(recordId);

            } catch (Exception e) {
                log.error("Error collecting student record completion metrics", e);
            }
        }
    }

    @Around("@annotation(com.edukit.common.annotation.AIGenerationMetrics)")
    public Object collectAIGenerationMetrics(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        if (args.length >= 2) {
            long memberId = (long) args[0];
            long recordId = (long) args[1];

            try {
                StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);

                // 첫 생성 여부 확인 (이 호출로 카운트도 증가됨)
                boolean isFirstGeneration = generationTrackingService.isFirstGeneration(recordId);

                // 전체 AI 생성 요청 카운트
                metricsService.recordAIGenerationRequest(studentRecord.getStudentRecordType());

                // 첫 생성 vs 재생성 구분 메트릭
                if (isFirstGeneration) {
                    metricsService.recordFirstGeneration(studentRecord.getStudentRecordType());
                    log.debug("First generation request for recordId: {}", recordId);
                } else {
                    metricsService.recordRegeneration(studentRecord.getStudentRecordType());
                    log.debug("Regeneration request for recordId: {}", recordId);
                }

                return joinPoint.proceed();

            } catch (Exception e) {
                log.error("Error collecting AI generation metrics", e);
                return joinPoint.proceed();
            }
        }

        return joinPoint.proceed();
    }
}
