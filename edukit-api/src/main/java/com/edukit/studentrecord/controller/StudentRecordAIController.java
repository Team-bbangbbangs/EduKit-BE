package com.edukit.studentrecord.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.studentrecord.controller.request.StudentRecordPromptRequest;
import com.edukit.studentrecord.facade.StudentRecordAIFacade;
import com.edukit.studentrecord.facade.response.StudentRecordTaskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        StudentRecordTaskResponse response = studentRecordAIFacade.getStreamingPrompt(memberId, recordId,
                request.byteCount(), request.prompt());
        return ResponseEntity.ok(EdukitResponse.success(response));
    }

    /*
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
                .map(response -> {
                    if (response.isFallback()) {
                        // fallback 응답인 경우 특별한 이벤트 타입 사용
                        return ServerSentEvent.<StudentRecordCreateResponse>builder()
                                .id(String.valueOf(response.versionNumber()))
                                .event("student-record-fallback")
                                .data(response)
                                .comment("AI 서비스 일시 장애로 인한 대체 응답입니다.")
                                .build();
                    } else {
                        // 정상 응답인 경우
                        return ServerSentEvent.<StudentRecordCreateResponse>builder()
                                .id(String.valueOf(response.versionNumber()))
                                .event("student-record-created")
                                .data(response)
                                .build();
                    }
                })
                .onErrorResume(throwable -> Flux.just(ServerSentEvent.<StudentRecordCreateResponse>builder()
                        .event("error")
                        .comment("스트리밍 중 오류가 발생했습니다: " + throwable.getMessage())
                        .build()));
    }

     */
}
