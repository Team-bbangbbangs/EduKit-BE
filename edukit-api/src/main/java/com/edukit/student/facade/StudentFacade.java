package com.edukit.student.facade;

import com.edukit.core.common.ExcelService;
import com.edukit.core.common.service.response.StudentExcelRow;
import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.service.StudentService;
import com.edukit.student.facade.response.StudentsCreateResponse;
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

    @Transactional
    public StudentsCreateResponse createStudentsFromExcel(final long memberId, final MultipartFile excelFile) {
        excelService.validateExcelFormat(excelFile);
        List<StudentExcelRow> studentRows = excelService.parseStudentExcel(excelFile);

        Member member = memberService.getMemberById(memberId);
        List<Student> createdStudents = studentService.createStudent(studentRows, member);

        return StudentsCreateResponse.from(createdStudents);
    }
}
