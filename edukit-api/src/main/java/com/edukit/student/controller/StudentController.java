package com.edukit.student.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.student.controller.request.StudentCreateRequest;
import com.edukit.student.controller.request.StudentDeleteRequest;
import com.edukit.student.controller.request.StudentUpdateRequest;
import com.edukit.student.facade.StudentFacade;
import com.edukit.student.facade.response.StudentUploadResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController implements StudentApi {

    private final StudentFacade studentFacade;

    @PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EdukitResponse<StudentUploadResponse>> uploadStudentExcel(@MemberId final long memberId,
                                                                                    @RequestParam("file") final MultipartFile file) {
        StudentUploadResponse response = studentFacade.createStudentsFromExcel(memberId, file);
        return ResponseEntity.ok().body(EdukitResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<EdukitResponse<Void>> createStudent(@MemberId final long memberId,
                                                              @RequestBody @Valid final StudentCreateRequest request) {
        List<StudentRecordType> recordTypes = request.recordTypes().stream().map(StudentRecordType::from).toList();
        studentFacade.createStudent(memberId, request.grade(), request.classNumber(), request.studentNumber(),
                request.studentName(), recordTypes);
        return ResponseEntity.ok().body(EdukitResponse.success());
    }

    @PatchMapping("/{studentId}")
    public ResponseEntity<EdukitResponse<Void>> updateStudent(@MemberId final long memberId,
                                                              @PathVariable final long studentId,
                                                              @RequestBody @Valid final StudentUpdateRequest request) {
        List<StudentRecordType> recordTypes = request.recordTypes().stream().map(StudentRecordType::from).toList();
        studentFacade.updateStudent(memberId, studentId, request.grade(), request.classNumber(),
                request.studentNumber(), request.studentName(), recordTypes);
        return ResponseEntity.ok().body(EdukitResponse.success());
    }

    @DeleteMapping
    public ResponseEntity<EdukitResponse<Void>> deleteStudents(@MemberId final long memberId,
                                                               @RequestBody @Valid final StudentDeleteRequest request) {
        studentFacade.deleteStudents(memberId, request.studentIds());
        return ResponseEntity.ok().body(EdukitResponse.success());
    }
}
