package com.edukit.student.facade.response;

import com.edukit.core.student.service.dto.InvalidStudentRow;
import java.util.Set;

public record StudentUploadResponse(
        int successCount,
        int failureCount,
        Set<InvalidStudentRow> invalidRows
) {
    public static StudentUploadResponse of(final int successCount, final int failureCount, final Set<InvalidStudentRow> invalidRows) {
        return new StudentUploadResponse(successCount, failureCount, invalidRows);
    }
}
