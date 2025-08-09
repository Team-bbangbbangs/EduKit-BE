package com.edukit.core.student.db.repository;

import com.edukit.core.student.db.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
