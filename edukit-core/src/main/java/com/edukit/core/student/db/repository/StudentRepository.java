package com.edukit.core.student.db.repository;

import com.edukit.core.student.db.entity.Student;
import com.edukit.core.member.db.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
    List<Student> findByMember(Member member);
}
