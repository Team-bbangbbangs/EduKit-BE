package com.edukit.studentrecord.facade;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.service.ExcelService;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.core.studentrecord.service.StudentRecordService;
import com.edukit.studentrecord.controller.request.StudentRecordDetailResponse;
import com.edukit.studentrecord.facade.response.StudentRecordsGetResponse;
import com.edukit.studentrecord.facade.response.StudentRecordsGetResponse.StudentRecordItems;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentRecordFacade {

    private final StudentRecordService studentRecordService;
    private final MemberService memberService;
    private final ExcelService excelService;

    @Transactional(readOnly = true)
    public StudentRecordsGetResponse getStudentRecords(final long memberId, final StudentRecordType recordType,
                                                       final Integer grade, final Integer classNumber,
                                                       final String search, final Long lastRecordId,
                                                       final int pageSize) {
        Member member = memberService.getMemberById(memberId);
        List<StudentRecord> studentRecords = studentRecordService.getStudentRecordsByFilters(member, recordType, grade,
                classNumber, search, lastRecordId, pageSize);

        return StudentRecordsGetResponse.of(studentRecords.stream()
                .map(record -> {
                    Student student = record.getStudent();
                    return StudentRecordItems.of(record.getId(), student.getGrade(), student.getClassNumber(),
                            student.getStudentNumber(), student.getStudentName(), record.getDescription());
                })
                .toList());
    }

    @Transactional
    public void updateStudentRecord(final long memberId, final long recordId, final String description) {
        StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);
        studentRecordService.updateStudentRecord(studentRecord, description);
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
