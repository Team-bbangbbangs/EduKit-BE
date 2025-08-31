package com.edukit.core.studentrecord.db.repository;

import com.edukit.core.student.db.entity.Student;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRecordRepository extends JpaRepository<StudentRecord, Long> {

    @Query("""
                SELECT sr FROM StudentRecord sr
                JOIN FETCH sr.student s
                WHERE s.member.id = :memberId
                  AND sr.studentRecordType = :studentRecordType
                  AND (:grade IS NULL OR s.grade = :grade)
                  AND (:classNumber IS NULL OR s.classNumber = :classNumber)
                  AND (:searchNormalized IS NULL
                     OR s.studentNameNormalized LIKE CONCAT('%', :searchNormalized, '%')
                     OR CAST(s.studentNumber AS string) LIKE CONCAT('%', :search, '%')
                  )
                  ORDER BY s.grade ASC, s.classNumber ASC, s.studentNumber ASC, s.studentName ASC, s.id ASC
            """)
    List<StudentRecord> findStudentRecordsByFilters(@Param("memberId") long memberId,
                                                    @Param("studentRecordType") StudentRecordType studentRecordType,
                                                    @Param("grade") Integer grade,
                                                    @Param("classNumber") Integer classNumber,
                                                    @Param("searchNormalized") String searchNormalized,
                                                    @Param("search") String search,
                                                    Pageable pageable);

    @Query("""
                SELECT sr FROM StudentRecord sr
                JOIN FETCH sr.student s
                WHERE s.member.id = :memberId
                  AND sr.studentRecordType = :studentRecordType
                  AND (:grade IS NULL OR s.grade = :grade)
                  AND (:classNumber IS NULL OR s.classNumber = :classNumber)
                  AND (:searchNormalized IS NULL
                     OR s.studentNameNormalized LIKE CONCAT('%', :searchNormalized, '%')
                     OR CAST(s.studentNumber AS string) LIKE CONCAT('%', :search, '%')
                  )
                  AND (s.grade > :cursorGrade
                       OR (s.grade = :cursorGrade AND s.classNumber > :cursorClassNumber)
                       OR (s.grade = :cursorGrade AND s.classNumber = :cursorClassNumber AND s.studentNumber > :cursorStudentNumber)
                       OR (s.grade = :cursorGrade AND s.classNumber = :cursorClassNumber AND s.studentNumber = :cursorStudentNumber AND s.studentName > :cursorStudentName)
                       OR (s.grade = :cursorGrade AND s.classNumber = :cursorClassNumber AND s.studentNumber = :cursorStudentNumber AND s.studentName = :cursorStudentName AND sr.id > :cursorId)
                  )
                  ORDER BY s.grade ASC, s.classNumber ASC, s.studentNumber ASC, s.studentName ASC, s.id ASC
            """)
    List<StudentRecord> findStudentRecordsByFilters(@Param("memberId") long memberId,
                                                    @Param("studentRecordType") StudentRecordType studentRecordType,
                                                    @Param("grade") Integer grade,
                                                    @Param("classNumber") Integer classNumber,
                                                    @Param("searchNormalized") String searchNormalized,
                                                    @Param("search") String search,
                                                    @Param("cursorId") Long cursorId,
                                                    @Param("cursorGrade") int cursorGrade,
                                                    @Param("cursorClassNumber") int cursorClassNumber,
                                                    @Param("cursorStudentNumber") int cursorStudentNumber,
                                                    @Param("cursorStudentName") String cursorStudentName,
                                                    Pageable pageable);

    @Query("""
              SELECT sr FROM StudentRecord sr
              JOIN FETCH sr.student s
              WHERE s.member.id = :memberId
                AND sr.studentRecordType = :studentRecordType
              ORDER BY s.grade, s.classNumber, s.studentNumber, s.studentName, sr.id
            """)
    List<StudentRecord> findByMemberIdAndStudentRecordType(@Param("memberId") Long memberId,
                                                           @Param("studentRecordType") StudentRecordType type);

    List<StudentRecord> findAllByStudent(Student student);

    int countByStudentMemberIdAndStudentRecordType(long memberId, StudentRecordType recordType);

    @Query("""
                select distinct s.grade
                from StudentRecord sr
                join sr.student s
                where s.member.id = :memberId
                  and sr.studentRecordType = :recordType
                order by s.grade
            """)
    List<Integer> findDistinctGradesByRecordType(@Param("memberId") long memberId,
                                                 @Param("recordType") StudentRecordType recordType
    );

    @Query("""
                select distinct s.classNumber
                from StudentRecord sr
                join sr.student s
                where s.member.id = :memberId
                  and sr.studentRecordType = :recordType
                order by s.classNumber
            """)
    List<Integer> findDistinctClassNumbersByRecordType(@Param("memberId") long memberId,
                                                       @Param("recordType") StudentRecordType recordType
    );
}
