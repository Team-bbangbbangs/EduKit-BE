package com.edukit.core.student.service.dto;

public record StudentExcelRow(
        int grade,
        int classNumber,
        int studentNumber,
        String studentName
) {
    public static StudentExcelRow of(final int grade, final int classNumber, final int studentNumber,
                                     final String studentName
    ) {
        return new StudentExcelRow(grade, classNumber, studentNumber, studentName);
    }
}
