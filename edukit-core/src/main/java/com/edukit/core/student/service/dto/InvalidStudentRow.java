package com.edukit.core.student.service.dto;

public record InvalidStudentRow(
        int rowNumber,
        int grade,
        int classNumber,
        int studentNumber,
        String studentName
) {
    public static InvalidStudentRow of(final int rowNumber, final int grade, final int classNumber,
                                       final int studentNumber, final String studentName) {
        return new InvalidStudentRow(rowNumber, grade, classNumber, studentNumber, studentName);
    }
}
