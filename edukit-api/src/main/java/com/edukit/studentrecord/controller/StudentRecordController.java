package com.edukit.studentrecord.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.studentrecord.controller.request.StudentRecordUpdateRequest;
import com.edukit.studentrecord.facade.StudentRecordFacade;
import com.edukit.studentrecord.facade.response.StudentRecordDetailResponse;
import com.edukit.studentrecord.facade.response.StudentRecordsGetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/student-records")
@RequiredArgsConstructor
public class StudentRecordController implements StudentRecordApi {

    private final StudentRecordFacade studentRecordFacade;

    private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @GetMapping("/{recordType}")
    public ResponseEntity<EdukitResponse<StudentRecordsGetResponse>> getStudentRecords(@MemberId final long memberId,
                                                                                       @PathVariable final StudentRecordType recordType,
                                                                                       @RequestParam(required = false) final Integer grade,
                                                                                       @RequestParam(required = false) final Integer classNumber,
                                                                                       @RequestParam(required = false) final String search,
                                                                                       @RequestParam(required = false) final Long lastRecordId) {
        StudentRecordsGetResponse response = studentRecordFacade.getStudentRecords(memberId, recordType, grade,
                classNumber, search, lastRecordId);
        return ResponseEntity.ok().body(EdukitResponse.success(response));
    }

    @PostMapping("/detail/{recordId}")
    public ResponseEntity<EdukitResponse<Void>> updateStudentRecord(@MemberId final long memberId,
                                                                    @PathVariable final long recordId,
                                                                    @RequestBody @Valid final StudentRecordUpdateRequest request) {
        // 오버로드 메서드 사용 (내부에서 타입 조회)
        studentRecordFacade.updateStudentRecord(memberId, recordId, request.description());
        return ResponseEntity.ok().body(EdukitResponse.success());
    }

    @GetMapping("/detail/{recordId}")
    public ResponseEntity<EdukitResponse<StudentRecordDetailResponse>> getStudentRecordDetail(
            @MemberId final long memberId, @PathVariable final long recordId) {
        StudentRecordDetailResponse response = studentRecordFacade.getStudentRecord(memberId, recordId);
        return ResponseEntity.ok().body(EdukitResponse.success(response));
    }

    @GetMapping("/{recordType}/excel")
    public ResponseEntity<byte[]> downloadStudentRecordExcel(@MemberId final long memberId,
                                                             @PathVariable final StudentRecordType recordType) {
        byte[] excelData = studentRecordFacade.downloadStudentRecordExcel(memberId, recordType);
        ContentDisposition contentDisposition = ContentDisposition
                .attachment()
                .filename(recordType.name() + ".xlsx")
                .build();
        return ResponseEntity.ok()
                .header("Content-Disposition", contentDisposition.toString())
                .header("Content-Type", XLSX_CONTENT_TYPE)
                .body(excelData);
    }
}
