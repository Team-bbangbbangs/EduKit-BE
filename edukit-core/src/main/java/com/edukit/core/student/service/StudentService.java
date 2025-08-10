package com.edukit.core.student.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.db.repository.StudentRepository;
import com.edukit.core.student.service.dto.StudentExcelRow;
import com.edukit.core.student.service.dto.StudentKey;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional
    public void createStudent(final Set<StudentExcelRow> studentRows, final Member member) {
        Set<StudentKey> existingKeys = getExistingStudents(member);

        List<Student> newStudents = studentRows.stream()
                .filter(row -> !existingKeys.contains(
                        StudentKey.from(row.grade(), row.classNumber(), row.studentNumber())))
                .map(row -> Student.create(member, row.grade(), row.classNumber(), row.studentNumber(),
                        row.studentName()))
                .toList();

        studentRepository.saveAll(newStudents);
    }

    private Set<StudentKey> getExistingStudents(final Member member) {
        List<Student> existingStudents = studentRepository.findByMember(member);

        return existingStudents.stream()
                .map(student -> StudentKey.from(student.getGrade(), student.getClassNumber(),
                        student.getStudentNumber()))
                .collect(Collectors.toSet());
    }
}
