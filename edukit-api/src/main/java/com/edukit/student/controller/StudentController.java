package com.edukit.student.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.student.facade.response.StudentUploadResponse;
import com.edukit.student.facade.StudentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController implements StudentApi {

    private final StudentFacade studentFacade;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EdukitResponse<StudentUploadResponse>> uploadStudentExcel(@MemberId long memberId,
                                                                                    @RequestParam("file") final MultipartFile file) {
        StudentUploadResponse response = studentFacade.createStudentsFromExcel(memberId, file);
        return ResponseEntity.ok().body(EdukitResponse.success(response));
    }
}
