package com.edukit.core.student.service.dto;

public record StudentKey(
        String grade,
        String classNumber,
        String studentNumber
) {
    public static StudentKey from(final String grade, final String classNumber, final String studentNumber) {
        return new StudentKey(grade, classNumber, studentNumber);
    }
}
