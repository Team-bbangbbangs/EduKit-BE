package com.edukit.core.student.service.dto;

public record StudentExcelRow(
        String grade,
        String classNumber,
        String studentNumber,
        String studentName
) {
}
