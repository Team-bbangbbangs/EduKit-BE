package com.edukit.core.student.service.dto;

import java.util.Objects;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StudentExcelRow that = (StudentExcelRow) o;
        return Objects.equals(grade, that.grade) && Objects.equals(classNumber, that.classNumber)
                && Objects.equals(studentName, that.studentName) && Objects.equals(studentNumber,
                that.studentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grade, classNumber, studentNumber, studentName);
    }
}
