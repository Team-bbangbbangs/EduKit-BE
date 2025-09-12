package com.edukit.core.student.service.dto;

public record StudentKey(
        int grade,
        int classNumber,
        int studentNumber
) {
    public static StudentKey from(final int grade, final int classNumber, final int studentNumber) {
        return new StudentKey(grade, classNumber, studentNumber);
    }
}
