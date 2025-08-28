package com.edukit.core.student.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.exception.MemberException;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.db.repository.StudentQueryRepository;
import com.edukit.core.student.db.repository.StudentRepository;
import com.edukit.core.student.exception.StudentErrorCode;
import com.edukit.core.student.exception.StudentException;
import com.edukit.core.student.service.dto.StudentItem;
import com.edukit.core.student.service.dto.StudentKey;
import com.edukit.core.student.service.dto.ValidStudentRow;
import com.edukit.core.student.utils.KoreanNormalizer;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentQueryRepository studentQueryRepository;
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

    @Transactional
    public Student createStudent(final int grade, final int classNumber, final int studentNumber,
                                 final String studentName, final Member member) {
        Student student = Student.create(member, grade, classNumber, studentNumber, studentName);
        validateStudent(student, member);
        try {
            return studentRepository.save(student);
        } catch (DataIntegrityViolationException e) {
            throw new StudentException(StudentErrorCode.STUDENT_ALREADY_EXIST_ERROR, e);
        }
    }


    public Student getStudent(final long studentId, final long memberId) {
        return studentRepository.findByIdAndMemberId(studentId, memberId)
                .orElseThrow(() -> new StudentException(StudentErrorCode.STUDENT_NOT_FOUND));
    }

    public List<Student> getStudents(final List<Long> studentIds, final long memberId) {
        List<Student> students = studentRepository.findByIdInAndMemberId(studentIds, memberId);
        if (students.size() != studentIds.size()) {
            throw new MemberException(StudentErrorCode.STUDENT_NOT_FOUND);
        }
        return students;
    }

    @Transactional
    public void updateStudent(final Student student, final int grade, final int classNumber, final int studentNumber,
                              final String studentName) {
        student.update(grade, classNumber, studentNumber, studentName);
    }

    @Transactional
    public void deleteStudents(final List<Long> studentIds) {
        studentRepository.deleteAllByIdInBatch(studentIds);
    }

    private void bulkInsertStudents(final List<ValidStudentRow> studentRows, final Member member) {
        String sql = """
                INSERT INTO student (member_id, grade, class_number, student_number, student_name, student_name_normalized, created_at, modified_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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
                    ps.setString(6, KoreanNormalizer.toNormalized(row.studentName()));
                    ps.setTimestamp(7, timestamp);
                    ps.setTimestamp(8, timestamp);
                });
    }

    private Set<StudentKey> getExistingStudents(final Member member) {
        List<Student> existingStudents = studentRepository.findByMember(member);

        return existingStudents.stream()
                .map(student -> StudentKey.from(student.getGrade(), student.getClassNumber(),
                        student.getStudentNumber()))
                .collect(Collectors.toSet());
    }

    private void validateStudent(final Student student, final Member member) {
        Set<StudentKey> existingKeys = getExistingStudents(member);
        if (existingKeys.contains(
                StudentKey.from(student.getGrade(), student.getClassNumber(), student.getStudentNumber()))) {
            throw new StudentException(StudentErrorCode.STUDENT_ALREADY_EXIST_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<StudentItem> getStudentsByFilters(final long memberId, final List<Integer> grades,
                                                  final List<Integer> classNumbers,
                                                  final List<StudentRecordType> recordTypes, final Long lastStudentId,
                                                  final int pageSize) {
        Optional<Student> lastStudent = studentRepository.findByIdAndMemberId(lastStudentId, memberId);
        return studentQueryRepository.findStudents(memberId, grades, classNumbers, recordTypes, lastStudent, pageSize);
    }
}
