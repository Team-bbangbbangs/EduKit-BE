package com.edukit.core.studentrecord.repository;

import com.edukit.core.studentrecord.entity.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRecordRepository extends JpaRepository<StudentRecord, Long> {
}
