package com.edukit.student.facade;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.student.service.ExcelService;
import com.edukit.core.student.service.StudentService;
import com.edukit.core.student.service.dto.StudentExcelRow;
import java.util.Set;
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
    public void createStudentsFromExcel(final long memberId, final MultipartFile excelFile) {
        excelService.validateExcelFormat(excelFile);
        Set<StudentExcelRow> studentRows = excelService.parseStudentExcel(excelFile);

        Member member = memberService.getMemberById(memberId);
        studentService.createStudent(studentRows, member);
    }
}
