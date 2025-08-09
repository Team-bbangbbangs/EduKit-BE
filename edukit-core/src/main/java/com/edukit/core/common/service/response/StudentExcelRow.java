package com.edukit.core.common.service.response;

public record StudentExcelRow(
        String grade,
        String classNumber,
        String studentNumber,
        String studentName
) {
}
