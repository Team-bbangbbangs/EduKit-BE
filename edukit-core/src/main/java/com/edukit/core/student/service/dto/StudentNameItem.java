package com.edukit.core.student.service.dto;

public record StudentNameItem(
        long recordId,
        String studentName
) {
    public static StudentNameItem of(final long recordId, final String studentName) {
        return new StudentNameItem(recordId, studentName);
    }
}
