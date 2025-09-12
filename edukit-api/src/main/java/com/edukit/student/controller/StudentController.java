package com.edukit.student.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.student.controller.request.StudentCreateRequest;
import com.edukit.student.controller.request.StudentDeleteRequest;
import com.edukit.student.controller.request.StudentUpdateRequest;
import com.edukit.student.facade.StudentFacade;
import com.edukit.student.facade.response.StudentUploadResponse;
import com.edukit.student.facade.response.StudentNamesGetResponse;
import com.edukit.student.facade.response.StudentsGetResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/students")
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

    @GetMapping
    public ResponseEntity<EdukitResponse<StudentsGetResponse>> getStudents(@MemberId final long memberId,
                                                                           @RequestParam(required = false) final List<Integer> grades,
                                                                           @RequestParam(required = false) final List<Integer> classNumbers,
                                                                           @RequestParam(required = false) final List<String> recordTypes,
                                                                           @RequestParam(required = false) final Long lastStudentId) {
        List<StudentRecordType> studentRecordTypes = Optional.ofNullable(recordTypes).orElseGet(List::of).stream()
                .map(StudentRecordType::from).toList();
        StudentsGetResponse response = studentFacade.getStudents(memberId, grades, classNumbers, studentRecordTypes,
                lastStudentId);
        return ResponseEntity.ok().body(EdukitResponse.success(response));
    }

    @GetMapping("/{recordType}")
    public ResponseEntity<EdukitResponse<StudentNamesGetResponse>> getStudentNames(
            @MemberId final long memberId,
            @PathVariable final StudentRecordType recordType,
            @RequestParam(required = false) final Integer grade,
            @RequestParam(required = false) final Integer classNumber,
            @RequestParam(required = false) final String studentName) {
        StudentNamesGetResponse response = studentFacade.getStudentNames(memberId, recordType, grade, classNumber,
                studentName);
        return ResponseEntity.ok().body(EdukitResponse.success(response));
    }
}
