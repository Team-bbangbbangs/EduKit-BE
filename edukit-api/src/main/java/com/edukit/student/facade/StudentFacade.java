package com.edukit.student.facade;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.service.ExcelService;
import com.edukit.core.student.service.StudentService;
import com.edukit.core.student.service.dto.ExcelParseResult;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.core.studentrecord.service.StudentRecordService;
import com.edukit.student.facade.response.StudentUploadResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StudentFacade {

    private final StudentService studentService;
    private final ExcelService excelService;
    private final MemberService memberService;
    private final StudentRecordService studentRecordService;

    @Transactional
    public StudentUploadResponse createStudentsFromExcel(final long memberId, final MultipartFile excelFile) {
        excelService.validateExcelFormat(excelFile);
        ExcelParseResult parseResult = excelService.parseStudentExcel(excelFile);

        Member member = memberService.getMemberById(memberId);

        if (!parseResult.validStudents().isEmpty()) {
            studentService.createStudent(parseResult.validStudents(), member);
        }

        return StudentUploadResponse.of(
                parseResult.validStudents().size(),
                parseResult.invalidRows().size(),
                parseResult.invalidRows()
        );
    }

    @Transactional
    public void createStudent(final long memberId, final int grade, final int classNumber, final int studentNumber,
                              final String studentName, final List<StudentRecordType> recordTypes) {
        Member member = memberService.getMemberById(memberId);
        Student student = studentService.createStudent(grade, classNumber, studentNumber, studentName, member);
        if (!recordTypes.isEmpty()) {
            studentRecordService.createStudentRecords(student, recordTypes);
        }
    }

    @Transactional
    public void updateStudent(final long memberId, final long studentId, final int grade, final int classNumber,
                              final int studentNumber, final String studentName,
                              final List<StudentRecordType> recordTypes) {
        Student student = studentService.getStudent(studentId, memberId);
        studentService.updateStudent(student, grade, classNumber, studentNumber, studentName);

        List<StudentRecord> studentRecords = studentRecordService.getStudentRecordsByStudent(student);
        studentRecordService.updateStudentRecord(recordTypes, studentRecords, student);
    }

    @Transactional
    public void deleteStudents(final long memberId, final List<Long> studentsIds) {
        List<Student> students = studentService.getStudents(studentsIds, memberId);
        List<Long> studentIds = students.stream().map(Student::getId).toList();

        studentRecordService.deleteStudentRecords(studentIds);
        studentService.deleteStudents(studentIds);
    }
}
