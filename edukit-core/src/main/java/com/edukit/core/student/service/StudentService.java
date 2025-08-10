package com.edukit.core.student.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.db.repository.StudentRepository;
import com.edukit.core.student.service.dto.StudentExcelRow;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> createStudent(final Set<StudentExcelRow> studentRows, final Member member) {
        List<Student> createdStudents = studentRows.stream()
                .map(row -> Student.create(
                        member,
                        row.grade(),
                        row.classNumber(),
                        row.studentNumber(),
                        row.studentName()
                ))
                .toList();

        studentRepository.saveAll(createdStudents);
        return createdStudents;
    }
}
