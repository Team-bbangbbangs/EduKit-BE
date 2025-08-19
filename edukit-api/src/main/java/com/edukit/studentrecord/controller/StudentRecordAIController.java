package com.edukit.studentrecord.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
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
public class StudentRecordAIController {

    private final StudentRecordAIFacade studentRecordAIFacade;

    @PostMapping("/ai-generate/{recordId}")
    public ResponseEntity<EdukitResponse<StudentRecordTaskResponse>> aiGenerateStudentRecord(
            @MemberId final long memberId,
            @PathVariable final long recordId,
            @RequestBody @Valid final StudentRecordPromptRequest request) {
        StudentRecordTaskResponse response = studentRecordAIFacade.createTaskId(memberId, recordId,
                request.byteCount(), request.prompt());
        return ResponseEntity.ok(EdukitResponse.success(response));
    }

    @GetMapping(value = "/stream/{taskId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStudentRecordResponse(
            @MemberId final long memberId,
            @PathVariable final long taskId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // sseEmitterManager.add(taskId, emitter);

        emitter.onCompletion(() -> {
            // sseEmitterManager.remove(taskId);
        });
        emitter.onTimeout(() -> {
            // sseEmitterManager.remove(taskId);
            emitter.completeWithError(new RuntimeException("SSE Timeout"));
            // sseEmitterManager.remove(taskId);
        });

        return emitter;
    }
}
