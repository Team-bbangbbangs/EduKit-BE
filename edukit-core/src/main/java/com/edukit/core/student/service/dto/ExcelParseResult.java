package com.edukit.core.student.service.dto;

import java.util.Set;

public record ExcelParseResult(
        Set<ValidStudentRow> validStudents,
        Set<InvalidStudentRow> invalidRows
) {
    public static ExcelParseResult of(final Set<ValidStudentRow> validStudents, final Set<InvalidStudentRow> invalidRows) {
        return new ExcelParseResult(validStudents, invalidRows);
    }
}
