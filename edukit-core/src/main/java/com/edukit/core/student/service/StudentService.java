package com.edukit.core.student.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.db.repository.StudentRepository;
import com.edukit.core.student.service.dto.ValidStudentRow;
import com.edukit.core.student.service.dto.StudentKey;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void createStudent(final Set<ValidStudentRow> studentRows, final Member member) {
        Set<StudentKey> existingKeys = getExistingStudents(member);

        List<ValidStudentRow> newStudentRows = studentRows.stream()
                .filter(row -> !existingKeys.contains(
                        StudentKey.from(row.grade(), row.classNumber(), row.studentNumber())))
                .toList();

        if (!newStudentRows.isEmpty()) {
            bulkInsertStudents(newStudentRows, member);
        }
    }

    private void bulkInsertStudents(final List<ValidStudentRow> studentRows, final Member member) {
        String sql = """
                INSERT INTO student (member_id, grade, class_number, student_number, student_name, created_at, modified_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        jdbcTemplate.batchUpdate(sql,
                studentRows,
                studentRows.size(),
                (ps, row) -> {
                    ps.setLong(1, member.getId());
                    ps.setInt(2, row.grade());
                    ps.setInt(3, row.classNumber());
                    ps.setInt(4, row.studentNumber());
                    ps.setString(5, row.studentName());
                    ps.setTimestamp(6, timestamp);
                    ps.setTimestamp(7, timestamp);
                });

    }

    private Set<StudentKey> getExistingStudents(final Member member) {
        List<Student> existingStudents = studentRepository.findByMember(member);

        return existingStudents.stream()
                .map(student -> StudentKey.from(student.getGrade(), student.getClassNumber(),
                        student.getStudentNumber()))
                .collect(Collectors.toSet());
    }
}
