package com.edukit.admin.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record NoticeCreateRequest(
        @Schema(description = "공지사항 카테고리", example = "ANNOUNCEMENT", allowableValues = {"ANNOUNCEMENT", "EVENT"})
        @NotNull(message = "카테고리는 필수입니다.")
        String category,
        
        @Schema(description = "공지사항 제목", example = "신규 기능 업데이트 안내")
        @NotNull(message = "제목은 필수입니다.")
        String title,
        
        @Schema(description = "공지사항 내용", example = "새로운 AI 생활기록부 생성 기능이 추가되었습니다.")
        @NotNull(message = "내용은 필수입니다.")
        String content,
        
        @Schema(description = "첨부 파일 키 목록", example = "[\"file1.jpg\", \"file2.pdf\"]")
        List<String> fileKeys
) {
}
