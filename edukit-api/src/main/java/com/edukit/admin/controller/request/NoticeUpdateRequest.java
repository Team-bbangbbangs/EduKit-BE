package com.edukit.admin.controller.request;

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
        
        @Schema(description = "추가된 파일 키 목록", example = "[\"newfile.jpg\"]")
        List<String> addedFileKeys,         //추가된 파일 목록
        
        @Schema(description = "삭제된 파일 ID 목록", example = "[1, 2]")
        List<Long> deletedNoticeFileIds     //삭제된 파일 목록
) {
}
