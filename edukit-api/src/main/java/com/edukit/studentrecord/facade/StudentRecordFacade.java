package com.edukit.studentrecord.facade;

import com.edukit.common.annotation.StudentRecordMetrics;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.service.ExcelService;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.core.studentrecord.service.StudentRecordService;
import com.edukit.studentrecord.facade.response.StudentRecordDetailResponse;
import com.edukit.studentrecord.facade.response.StudentRecordsGetResponse;
import com.edukit.studentrecord.facade.response.StudentRecordsGetResponse.StudentRecordItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentRecordFacade {

    private final StudentRecordService studentRecordService;
    private final ExcelService excelService;

    @Transactional(readOnly = true)
    public StudentRecordsGetResponse getStudentRecords(final long memberId, final StudentRecordType recordType,
                                                       final Integer grade, final Integer classNumber,
                                                       final String search, final Long lastRecordId) {
        List<StudentRecord> studentRecords = studentRecordService.getStudentRecordsByFilters(memberId, recordType,
                grade, classNumber, search, lastRecordId);
        List<StudentRecordItem> studentRecordItems = studentRecords.stream()
                .map(record -> {
                    Student student = record.getStudent();
                    return StudentRecordItem.of(record.getId(), student.getGrade(), student.getClassNumber(),
                            student.getStudentNumber(), student.getStudentName(), record.getDescription());
                }).toList();

        int studentCount = studentRecordService.getStudentCountByRecordType(memberId, recordType);
        List<Integer> studentGrades = studentRecordService.getStudentGrades(memberId, recordType);
        List<Integer> studentClassNumbers = studentRecordService.getStudentClassNumbers(memberId, recordType);

        return StudentRecordsGetResponse.of(studentCount, studentGrades, studentClassNumbers, studentRecordItems);
    }

    @Transactional
    @StudentRecordMetrics
    public void updateStudentRecord(final long memberId, final long recordId,
                                    final StudentRecordType recordType, final String description) {
        StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);
        studentRecordService.updateStudentRecord(studentRecord, description);
    }

    // 컨트롤러 호환성을 위한 오버로드 메서드
    @Transactional
    public void updateStudentRecord(final long memberId, final long recordId, final String description) {
        StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);
        // 타입을 조회한 후 메트릭 포함 메서드 호출
        updateStudentRecord(memberId, recordId, studentRecord.getStudentRecordType(), description);
    }

    @Transactional(readOnly = true)
    public StudentRecordDetailResponse getStudentRecord(final long memberId, final long recordId) {
        StudentRecord recordDetail = studentRecordService.getRecordDetail(memberId, recordId);
        return StudentRecordDetailResponse.of(recordDetail.getDescription());
    }

    public byte[] downloadStudentRecordExcel(final long memberId, final StudentRecordType recordType) {
        List<StudentRecord> studentRecords = studentRecordService.getAllStudentRecordsByType(memberId, recordType);
        return excelService.generateStudentRecordExcel(studentRecords, recordType);
    }
}
