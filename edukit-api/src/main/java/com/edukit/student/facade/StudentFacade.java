package com.edukit.student.facade;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.student.service.ExcelService;
import com.edukit.core.student.service.StudentService;
import com.edukit.core.student.service.dto.ExcelParseResult;
import com.edukit.student.facade.response.StudentUploadResponse;
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
}
