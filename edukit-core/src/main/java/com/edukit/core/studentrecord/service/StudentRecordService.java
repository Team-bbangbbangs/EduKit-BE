package com.edukit.core.studentrecord.service;

import com.edukit.core.studentrecord.db.entity.Student;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import com.edukit.core.studentrecord.exception.StudentRecordErrorCode;
import com.edukit.core.studentrecord.exception.StudentRecordException;
import com.edukit.core.studentrecord.db.repository.StudentRecordAITaskRepository;
import com.edukit.core.studentrecord.db.repository.StudentRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentRecordService {

    private final StudentRecordRepository studentRecordRepository;
    private final StudentRecordAITaskRepository aiTaskRepository;

    @Transactional(readOnly = true)
    public StudentRecord getRecordDetail(final long memberId, final long recordId) {
        StudentRecord existingDetail = getRecordDetailById(recordId);
        validatePermission(existingDetail.getStudent(), memberId);
        return existingDetail;
    }

    @Transactional
    public long createAITask(final StudentRecord studentRecord, final String prompt) {
        StudentRecordAITask aiTask = StudentRecordAITask.create(studentRecord, prompt);
        aiTaskRepository.save(aiTask);
        return aiTask.getId();
    }

    private StudentRecord getRecordDetailById(final long recordId) {
        return studentRecordRepository.findById(recordId)
                .orElseThrow(() -> new StudentRecordException(StudentRecordErrorCode.STUDENT_RECORD_NOT_FOUND));
    }

    private void validatePermission(final Student student, final long memberId) {
        if (student.getMember().getId() != memberId) {
            throw new StudentRecordException(StudentRecordErrorCode.PERMISSION_DENIED);
        }
    }
}
