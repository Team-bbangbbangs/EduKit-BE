package com.edukit.core.studentrecord.db.repository;

import com.edukit.core.studentrecord.db.entity.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRecordRepository extends JpaRepository<StudentRecord, Long> {
}
