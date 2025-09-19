package com.edukit.studentrecord.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.exception.StudentRecordErrorCode;
import com.edukit.core.studentrecord.exception.StudentRecordException;
import com.edukit.core.studentrecord.service.StudentRecordService;
import com.edukit.studentrecord.controller.request.StudentRecordPromptRequest;
import com.edukit.studentrecord.facade.StudentRecordAIFacade;
import com.edukit.studentrecord.facade.response.StudentRecordTaskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v2/student-records")
@RequiredArgsConstructor
public class StudentRecordAIController implements StudentRecordAIApi {

    private final StudentRecordAIFacade studentRecordAIFacade;
    private final StudentRecordService studentRecordService;

    @PostMapping("/ai-generate/{recordId}")
    public ResponseEntity<EdukitResponse<StudentRecordTaskResponse>> aiGenerateStudentRecord(
            @MemberId final long memberId,
            @PathVariable final long recordId,
            @RequestBody @Valid final StudentRecordPromptRequest request) {
        // 컨트롤러에서 직접 타입 조회 (1회 DB 조회)
        StudentRecord studentRecord = studentRecordService.getRecordDetail(memberId, recordId);

        // 타입과 함께 호출 (AOP에서 DB 조회 없음)
        StudentRecordTaskResponse response = studentRecordAIFacade.createTaskId(memberId, recordId,
                studentRecord.getStudentRecordType(), request.byteCount(), request.prompt());
        return ResponseEntity.ok(EdukitResponse.success(response));
    }

    @GetMapping(value = "/stream/{taskId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStudentRecordResponse(@MemberId final long memberId, @PathVariable final String taskId) {

        SseEmitter emitter = studentRecordAIFacade.createChannel(memberId, taskId);

        emitter.onCompletion(() -> {
            studentRecordAIFacade.closeChannel(taskId);
        });
        emitter.onTimeout(() -> {
            emitter.completeWithError(new StudentRecordException(StudentRecordErrorCode.AI_GENERATE_TIMEOUT));
            studentRecordAIFacade.closeChannel(taskId);
        });
        emitter.onError((throwable) -> {
            studentRecordAIFacade.closeChannel(taskId);
        });

        return emitter;
    }
}
