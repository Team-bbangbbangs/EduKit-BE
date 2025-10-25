package com.edukit.core.point.db.enums;

public enum PointTransactionType {
    CHARGE,          // 충전 (결제 등)
    DEDUCT,          // 차감 (AI 생성 등)
    REFUND,          // 환불
    COMPENSATION,    // 보상 (실패 시 복구)
    ADMIN_ADJUST     // 관리자 조정
}
