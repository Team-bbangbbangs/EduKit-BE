package com.edukit.core.student.service.dto;

import java.util.Objects;

public record StudentExcelRow(
        String grade,
        String classNumber,
        String studentNumber,
        String studentName
) {
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
