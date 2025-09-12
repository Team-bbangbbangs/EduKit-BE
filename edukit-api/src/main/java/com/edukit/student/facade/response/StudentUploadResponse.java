package com.edukit.student.facade.response;

import com.edukit.core.student.service.dto.InvalidStudentRow;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

public record StudentUploadResponse(
        @Schema(description = "성공적으로 등록된 학생 수", example = "15")
        int successCount,
        
        @Schema(description = "등록 실패한 학생 수", example = "3")
        int failureCount,
        
        @Schema(description = "잘못된 데이터를 가진 학생 행 정보")
        Set<InvalidStudentRow> invalidRows
) {
    public static StudentUploadResponse of(final int successCount, final int failureCount, final Set<InvalidStudentRow> invalidRows) {
        return new StudentUploadResponse(successCount, failureCount, invalidRows);
    }
}
