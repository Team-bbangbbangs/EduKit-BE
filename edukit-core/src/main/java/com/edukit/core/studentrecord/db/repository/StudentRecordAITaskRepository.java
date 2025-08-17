package com.edukit.core.studentrecord.db.repository;

import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRecordAITaskRepository extends JpaRepository<StudentRecordAITask, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from StudentRecordAITask t where t.studentRecord.id in :recordIds")
    void deleteAllByStudentRecordIdIn(@Param("recordIds") List<Long> recordIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from StudentRecordAITask t where t.studentRecord.student.id in :studentIds")
    void deleteAllByStudentIds(@Param("studentIds") List<Long> studentIds);
}
