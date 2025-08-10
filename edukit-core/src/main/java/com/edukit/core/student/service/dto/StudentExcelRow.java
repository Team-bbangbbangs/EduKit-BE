package com.edukit.core.student.service.dto;

public record StudentExcelRow(
        String grade,
        String classNumber,
        String studentNumber,
        String studentName
) {
    public static StudentExcelRow of(final String grade, final String classNumber, final String studentNumber,
                                     final String studentName
    ) {
        return new StudentExcelRow(grade, classNumber, studentNumber, studentName);
    }
}
