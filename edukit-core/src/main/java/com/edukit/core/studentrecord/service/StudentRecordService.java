package com.edukit.core.studentrecord.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.utils.KoreanSearchUtils;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.core.studentrecord.db.repository.StudentRecordAITaskRepository;
import com.edukit.core.studentrecord.db.repository.StudentRecordRepository;
import com.edukit.core.studentrecord.exception.StudentRecordErrorCode;
import com.edukit.core.studentrecord.exception.StudentRecordException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Transactional(readOnly = true)
    public List<StudentRecord> getStudentRecordsByFilters(final Member member,
                                                          final StudentRecordType studentRecordType,
                                                          final Integer grade, final Integer classNumber,
                                                          final String search, final Long lastRecordId,
                                                          final int pageSize) {
        String normalizedSearch = KoreanSearchUtils.toSearchText(search);
        Pageable pageable = PageRequest.of(0, pageSize, Sort.unsorted());   //페이지 사이즈 용도

        if (lastRecordId == null) {
            return studentRecordRepository.findStudentRecordsByFilters(member, studentRecordType, grade, classNumber,
                    normalizedSearch, search, pageable);
        }

        StudentRecord lastRecord = getRecordDetailById(lastRecordId);
        Student lastStudent = lastRecord.getStudent();
        return studentRecordRepository.findStudentRecordsByFilters(member, studentRecordType, grade, classNumber,
                normalizedSearch, search, lastRecord.getId(), lastStudent.getGrade(),
                lastStudent.getClassNumber(), lastStudent.getStudentNumber(), lastStudent.getStudentName(), pageable);
    }

    @Transactional
    public void updateStudentRecord(final StudentRecord studentRecord, final String description) {
        studentRecord.updateDescription(description);
    }

    @Transactional(readOnly = true)
    public List<StudentRecord> getAllStudentRecordsByType(final long memberId, final StudentRecordType recordType) {
        return studentRecordRepository.findByMemberIdAndStudentRecordType(memberId, recordType);
    }
}
