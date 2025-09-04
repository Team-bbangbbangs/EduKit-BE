package com.edukit.core.student.db.repository;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.student.db.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByMember(Member member);

    Optional<Student> findByIdAndMemberId(Long studentId, long memberId);

    List<Student> findByIdInAndMemberId(List<Long> studentIds, long memberId);

    int countByMemberId(long memberId);

    @Query("select distinct s.grade from Student s " +
            "where s.member.id = :memberId " +
            "order by s.grade")
    List<Integer> findAllGrades(@Param("memberId") long memberId);

    @Query("select distinct s.classNumber from Student s " +
            "where s.member.id = :memberId " +
            "order by s.classNumber")
    List<Integer> findAllClasses(@Param("memberId") long memberId);

    Optional<Student> findByMemberIdAndGradeAndClassNumberAndStudentNumber(long memberId, int grade, int classNumber,
                                                                           int studentNumber);
}
