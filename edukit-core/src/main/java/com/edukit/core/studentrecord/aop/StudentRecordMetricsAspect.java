package com.edukit.core.studentrecord.aop;

import com.edukit.core.studentrecord.db.entity.StudentRecord;
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

                metricsService.recordAIGenerationRequest(studentRecord.getStudentRecordType());

                return joinPoint.proceed();

            } catch (Exception e) {
                log.error("Error collecting AI generation metrics", e);
                return joinPoint.proceed();
            }
        }

        return joinPoint.proceed();
    }
}
