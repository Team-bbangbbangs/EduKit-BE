package com.edukit.core.studentrecord.db.enums;

public enum AITaskStatus {
    PENDING,        // 생성됨
    IN_PROGRESS,    // SQS 전송됨, Lambda 처리 중
    COMPLETED,      // 성공
    FAILED        // 실패 (보상 트랜잭션 대상)
}
