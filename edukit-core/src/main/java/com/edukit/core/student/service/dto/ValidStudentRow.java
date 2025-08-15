package com.edukit.core.student.service.dto;

public record ValidStudentRow(
        int grade,
        int classNumber,
        int studentNumber,
        String studentName
) {
    public static ValidStudentRow of(final int grade, final int classNumber, final int studentNumber,
                                     final String studentName
    ) {
        return new ValidStudentRow(grade, classNumber, studentNumber, studentName);
    }
}
