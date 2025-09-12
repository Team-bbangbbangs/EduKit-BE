package com.edukit.core.studentrecord.db.repository;

import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRecordAITaskRepository extends JpaRepository<StudentRecordAITask, Long> {

    boolean existsByIdAndMemberId(long taskId, long memberId);
}
