package com.edukit.core.student.service.dto;

import java.util.List;

public record StudentItem(
        long studentId,
        int grade,
        int classNumber,
        int studentNumber,
        String studentName,
        List<String> recordTypes
) {
    public static StudentItem of(final long studentId, final int grade, final int classNumber, final int studentNumber,
                                 final String studentName, final List<String> recordTypes) {
        return new StudentItem(studentId, grade, classNumber, studentNumber, studentName, recordTypes);
    }
}
