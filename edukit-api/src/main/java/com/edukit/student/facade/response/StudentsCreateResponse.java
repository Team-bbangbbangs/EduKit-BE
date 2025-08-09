package com.edukit.student.facade.response;

import com.edukit.core.student.db.entity.Student;
import java.util.List;

public record StudentsCreateResponse(
        List<StudentId> studentIds
) {
    public static StudentsCreateResponse from(final List<Student> students) {
        return new StudentsCreateResponse(
                students.stream()
                        .map(student -> new StudentId(student.getId()))
                        .toList()
        );
    }

    private record StudentId(long studentId) {
    }
}

