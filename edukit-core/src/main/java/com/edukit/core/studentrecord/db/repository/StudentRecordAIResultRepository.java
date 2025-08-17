package com.edukit.core.studentrecord.db.repository;

import com.edukit.core.studentrecord.db.entity.StudentRecordAIResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRecordAIResultRepository extends JpaRepository<StudentRecordAIResult, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from StudentRecordAIResult r where r.studentRecordAITask.studentRecord.id in :recordIds")
    void deleteAllByStudentRecordIds(@Param("recordIds") List<Long> recordIds);
}
