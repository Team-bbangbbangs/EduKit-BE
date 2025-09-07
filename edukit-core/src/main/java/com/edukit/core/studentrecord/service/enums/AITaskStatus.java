package com.edukit.core.studentrecord.service.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AITaskStatus {

    PHASE1_STARTED("PHASE1_STARTED", "3가지 버전 생성 중"),
    PHASE1_COMPLETED("PHASE1_COMPLETED", "3가지 버전 생성 완료");

    private final String status;
    private final String message;

    public static String getMessageByStatus(final String status) {
        return Arrays.stream(values())
                .filter(aiTaskStatus -> aiTaskStatus.getStatus().equals(status))
                .map(AITaskStatus::getMessage)
                .findFirst()
                .orElse(null);
    }
}
