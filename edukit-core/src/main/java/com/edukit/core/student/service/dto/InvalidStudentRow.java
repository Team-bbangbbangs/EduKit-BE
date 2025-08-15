package com.edukit.core.student.service.dto;

public record InvalidStudentRow(
        int rowNumber,
        String grade,
        String classNumber,
        String studentNumber,
        String studentName
) {
    public static InvalidStudentRow of(final int rowNumber, final String grade, final String classNumber,
                                       final String studentNumber, final String studentName) {
        return new InvalidStudentRow(rowNumber, grade, classNumber, studentNumber, studentName);
    }
}
