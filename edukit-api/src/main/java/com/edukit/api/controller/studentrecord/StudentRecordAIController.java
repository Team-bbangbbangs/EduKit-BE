package com.edukit.api.controller.studentrecord;

import com.edukit.api.common.annotation.MemberId;
import com.edukit.api.controller.studentrecord.request.StudentRecordPromptRequest;
import com.edukit.core.auth.facade.AuthFacade;
import com.edukit.core.studentrecord.facade.StudentRecordAIFacade;
import com.edukit.core.studentrecord.facade.response.StudentRecordCreateResponse;
import com.edukit.core.studentrecord.facade.response.StudentRecordTaskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v2/student-records")
@RequiredArgsConstructor
public class StudentRecordAIController {

    private final StudentRecordAIFacade studentRecordAIFacade;
    private final AuthFacade authFacade;

    /* v1.0.0
    @PostMapping("/ai-generate/{recordId}")
    public ResponseEntity<EdukitResponse<StudentRecordCreateResponse>> aiGenerateStudentRecord(
            @MemberId final long memberId,
            @PathVariable final long recordId,
            @RequestBody @Valid final StudentRecordPromptRequest request
    ) {
        StudentRecordTaskResponse promptResponse = studentRecordAIFacade.getPrompt(memberId, recordId,
                request.byteCount(), request.prompt());
        StudentRecordCreateResponse response = studentRecordAIFacade.generateAIStudentRecord(
                promptResponse.inputPrompt());
        return ResponseEntity.ok(EdukitResponse.success(response));
    }
     */

    @PostMapping(value = "/ai-generate/{recordId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StudentRecordCreateResponse>> aiGenerateStudentRecordStream(
            @MemberId final long memberId,
            @PathVariable final long recordId,
            @RequestBody @Valid final StudentRecordPromptRequest request
    ) {
        authFacade.checkHasPermission(memberId);
        StudentRecordTaskResponse promptResponse = studentRecordAIFacade.getStreamingPrompt(memberId, recordId,
                request.byteCount(), request.prompt());

        return studentRecordAIFacade.generateAIStudentRecordStream(promptResponse.inputPrompt())
                .map(response -> ServerSentEvent.<StudentRecordCreateResponse>builder()
                        .id(String.valueOf(response.versionNumber()))
                        .event("student-record-created")
                        .data(response)
                        .build());
    }
}
