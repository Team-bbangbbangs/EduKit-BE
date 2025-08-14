package com.edukit.studentrecord.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.studentrecord.controller.request.StudentRecordUpdateRequest;
import com.edukit.studentrecord.facade.StudentRecordFacade;
import com.edukit.studentrecord.facade.response.StudentRecordsGetResponse;
import lombok.RequiredArgsConstructor;
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
public class StudentRecordController {

    private static final String DEFAULT_PAGE_SIZE = "10";
    private final StudentRecordFacade studentRecordFacade;

    @GetMapping("/{recordType}")
    public ResponseEntity<EdukitResponse<StudentRecordsGetResponse>> getStudentRecords(@MemberId final long memberId,
                                                                                       @PathVariable final StudentRecordType recordType,
                                                                                       @RequestParam(required = false) final Integer grade,
                                                                                       @RequestParam(required = false) final Integer classNumber,
                                                                                       @RequestParam(required = false) final String search,
                                                                                       @RequestParam(required = false) final Long lastRecordId,
                                                                                       @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize) {
        return ResponseEntity.ok().body(EdukitResponse.success(
                studentRecordFacade.getStudentRecords(memberId, recordType, grade, classNumber, search, lastRecordId,
                        pageSize)
        ));
    }

    @PostMapping("/{recordId}")
    public ResponseEntity<EdukitResponse<Void>> updateStudentRecord(@MemberId final long memberId,
                                                                    @PathVariable final long recordId,
                                                                    @RequestBody final StudentRecordUpdateRequest request) {
        studentRecordFacade.updateStudentRecord(memberId, recordId, request.description());
        return ResponseEntity.ok().body(EdukitResponse.success());
    }
}
