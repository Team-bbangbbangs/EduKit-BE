package com.edukit.core.studentrecord.db.enums;

import com.edukit.core.studentrecord.exception.StudentRecordErrorCode;
import com.edukit.core.studentrecord.exception.StudentRecordException;
import java.util.Arrays;

public enum StudentRecordType {
    SUBJECT,    // 세부능력 및 특기사항
    BEHAVIOR,   // 행동특성 및 종합의견
    CAREER,     // 창의적 체험활동 - 진로
    FREE,       // 창의적 체험활동 - 자율
    CLUB        // 창의적 체험활동 - 동아리
    ;

    public static StudentRecordType from(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new StudentRecordException(StudentRecordErrorCode.STUDENT_RECORD_TYPE_NOT_FOUND));
    }
}
