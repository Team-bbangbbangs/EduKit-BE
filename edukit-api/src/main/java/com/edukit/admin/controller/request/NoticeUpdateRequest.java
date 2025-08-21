package com.edukit.admin.controller.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record NoticeUpdateRequest(
        @Schema(description = "공지사항 카테고리", example = "EVENT", allowableValues = {"ANNOUNCEMENT", "EVENT"})
        @NotNull(message = "카테고리는 필수입니다.")
        String category,
        
        @Schema(description = "공지사항 제목", example = "수정된 공지사항 제목")
        @NotNull(message = "제목은 필수입니다.")
        String title,
        
        @Schema(description = "공지사항 내용", example = "수정된 공지사항 내용입니다.")
        @NotNull(message = "내용은 필수입니다.")
        String content,
        
        @Schema(description = "게시물에 포함된 파일 키 목록", example = "[\"newfile.jpg\"]")
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        List<String> fileKeys
) {
}
