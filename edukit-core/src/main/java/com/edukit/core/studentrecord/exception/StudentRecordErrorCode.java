package com.edukit.core.studentrecord.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudentRecordErrorCode implements ErrorCode {

    AI("SR-40401", "해당 학생 기록이 존재하지 않습니다."),
    PERMISSION_DENIED("SR-40302", "해당 학생 기록에 대한 권한이 없습니다."),
    STUDENT_RECORD_TYPE_NOT_FOUND("SR-40403", "유효하지 않은 생활기록부 항목입니다."),
    DUPLICATE_RECORD_TYPE("SR-40004", "중복된 생활기록부 유형이 포함되어 있습니다."),
    MESSAGE_PROCESSING_FAILED("SR-50005", "Redis 메시지 처리 중 오류가 발생했습니다."),
    AI_TASK_NOT_FOUND("SR-40406", "AI 작업을 찾을 수 없습니다."),
    AI_TASK_COMPLETION_FAILED("SR-50007", "AI 작업 완료에 실패했습니다."),
    AI_GENERATE_TIMEOUT("SR-50008", "AI 생성 요청이 시간 초과되었습니다.");

    private final String code;
    private final String message;
}
