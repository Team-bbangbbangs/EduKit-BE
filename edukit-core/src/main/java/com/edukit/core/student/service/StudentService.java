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

    public void createStudent(final Set<StudentExcelRow> studentRows, final Member member) {
        List<Student> existingStudents = studentRepository.findByMember(member);

        List<Student> newStudents = studentRows.stream()
                .filter(row -> !isDuplicateStudent(row, existingStudents))
                .map(row -> Student.create(member, row.grade(), row.classNumber(), row.studentNumber(), row.studentName()))
                .toList();

        studentRepository.saveAll(newStudents);
    }

    private boolean isDuplicateStudent(final StudentExcelRow row, final List<Student> existingStudents) {
        return existingStudents.stream()
                .anyMatch(student ->
                        student.getGrade().equals(row.grade()) &&
                                student.getClassNumber().equals(row.classNumber()) &&
                                student.getStudentNumber().equals(row.studentNumber()) &&
                                student.getStudentName().equals(row.studentName())
                );
    }
}
