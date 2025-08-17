package com.edukit.core.student.db.repository;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.student.db.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
    List<Student> findByMember(Member member);

    Optional<Student> findByIdAndMemberId(long studentId, long memberId);
}
