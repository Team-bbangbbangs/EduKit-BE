package com.edukit.core.studentrecord.service.enums;

import com.edukit.core.studentrecord.exception.StudentRecordErrorCode;
import com.edukit.core.studentrecord.exception.StudentRecordException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AITaskStatus {
    PHASE1_STARTED("PHASE1_STARTED", "3가지 버전 생성 중"),
    PHASE2_STARTED("PHASE2_STARTED", "금칙어 필터링 진행"),
    PHASE3_STARTED("PHASE3_STARTED", "바이트 수 최적화 진행"),
    COMPLETED("COMPLETED", "생성 완료");

    private final String status;
    private final String message;

    public static String getMessageByStatus(final String status) {
        return Arrays.stream(values())
                .filter(aiTaskStatus -> aiTaskStatus.getStatus().equals(status))
                .map(AITaskStatus::getMessage)
                .findFirst()
                .orElseThrow(() -> new StudentRecordException(StudentRecordErrorCode.INVALID_AI_TASK_STATUS));
    }

    public static boolean isInProgress(final String currentStatus) {
        return !COMPLETED.getStatus().equals(currentStatus);
    }

    public static boolean isComplete(final String status) {
        return COMPLETED.getStatus().equals(status);
    }
}
