package com.edukit.core.student.db.repository;

import com.edukit.core.student.db.entity.QStudent;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.service.dto.StudentItem;
import com.edukit.core.studentrecord.db.entity.QStudentRecord;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudentQueryRepository {

    private final JPAQueryFactory qf;

    public List<StudentItem> findStudents(final Long memberId,
                                          final List<Integer> grades,
                                          final List<Integer> classNumbers,
                                          final List<StudentRecordType> recordTypes,
                                          final Optional<Student> lastStudent,
                                          final int pageSize
    ) {
        QStudent qStudent = QStudent.student;

        // 기본 쿼리 구성
        JPAQuery<Student> query = qf.selectFrom(qStudent);

        applyWhereConditions(query, qStudent, memberId, grades, classNumbers, lastStudent);
        applyRecordTypesFilter(query, qStudent, memberId, recordTypes);

        // 학생 목록 조회
        List<Student> students = fetchStudents(query, qStudent, pageSize);
        if (students.isEmpty()) {
            return List.of();
        }

        // 조회한 학생들의 recordTypes 일괄 조회
        Map<Long, List<String>> typeMap = fetchRecordTypes(students);
        return generateStudentItems(students, typeMap);
    }

    private void applyWhereConditions(
            final JPAQuery<Student> query,
            final QStudent qStudent,
            final Long memberId,
            final List<Integer> grades,
            final List<Integer> classNumbers,
            final Optional<Student> lastStudent
    ) {
        BooleanBuilder where = new BooleanBuilder().and(qStudent.member.id.eq(memberId));

        if (grades != null && !grades.isEmpty()) {
            where.and(qStudent.grade.in(grades));
        }
        if (classNumbers != null && !classNumbers.isEmpty()) {
            where.and(qStudent.classNumber.in(classNumbers));
        }
        lastStudent.ifPresent(ls -> where.and(generateCursor(qStudent, ls)));

        query.where(where);
    }

    private BooleanExpression generateCursor(final QStudent qStudent, final Student lastStudent) {
        return qStudent.grade.gt(lastStudent.getGrade())
                .or(qStudent.grade.eq(lastStudent.getGrade())
                        .and(qStudent.classNumber.gt(lastStudent.getClassNumber())))
                .or(qStudent.grade.eq(lastStudent.getGrade()).and(qStudent.classNumber.eq(lastStudent.getClassNumber()))
                        .and(qStudent.studentNumber.gt(lastStudent.getStudentNumber())));
    }

    private void applyRecordTypesFilter(
            final JPAQuery<Student> query,
            final QStudent qStudent,
            final Long memberId,
            final List<StudentRecordType> recordTypes
    ) {
        if (recordTypes == null || recordTypes.isEmpty()) {
            return;
        }

        QStudentRecord qStudentRecord = QStudentRecord.studentRecord;
        query.leftJoin(qStudentRecord).on(qStudentRecord.student.id.eq(qStudent.id)
                        .and(qStudentRecord.student.member.id.eq(memberId))
                        .and(qStudentRecord.studentRecordType.in(recordTypes))
                )
                .groupBy(qStudent.id, qStudent.grade, qStudent.classNumber, qStudent.studentNumber)
                .having(qStudentRecord.studentRecordType.countDistinct().eq((long) recordTypes.size()));
    }

    private List<Student> fetchStudents(
            final JPAQuery<Student> query,
            final QStudent qStudent,
            final int pageSize
    ) {
        return query
                .orderBy(qStudent.grade.asc(), qStudent.classNumber.asc(), qStudent.studentNumber.asc())
                .limit(pageSize)
                .fetch();
    }

    private Map<Long, List<String>> fetchRecordTypes(final List<Student> students) {
        QStudentRecord qStudentRecord = QStudentRecord.studentRecord;
        List<Long> studentIds = students.stream().map(Student::getId).toList();
        if (studentIds.isEmpty()) {
            return Map.of();
        }

        List<Tuple> studentRecords = qf.select(qStudentRecord.student.id, qStudentRecord.studentRecordType)
                .from(qStudentRecord)
                .where(qStudentRecord.student.id.in(studentIds))
                .fetch();

        Map<Long, List<String>> recordMap = new HashMap<>();
        for (Tuple studentRecordTuple : studentRecords) {
            Long studentId = studentRecordTuple.get(qStudentRecord.student.id);
            recordMap.computeIfAbsent(studentId, k -> new ArrayList<>())
                    .add(Objects.requireNonNull(studentRecordTuple.get(qStudentRecord.studentRecordType)).name());
        }
        return recordMap;
    }

    private List<StudentItem> generateStudentItems(final List<Student> students,
                                                   final Map<Long, List<String>> typeMap) {
        return students.stream()
                .map(student -> StudentItem.of(
                        student.getId(),
                        student.getGrade(),
                        student.getClassNumber(),
                        student.getStudentNumber(),
                        student.getStudentName(),
                        typeMap.getOrDefault(student.getId(), List.of())
                ))
                .toList();
    }
}
