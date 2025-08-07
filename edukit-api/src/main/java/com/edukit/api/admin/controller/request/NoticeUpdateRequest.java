package com.edukit.api.admin.controller.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record NoticeUpdateRequest(
        @NotNull(message = "카테고리 ID는 필수입니다.")
        Integer categoryId,
        @NotNull(message = "제목은 필수입니다.")
        String title,
        @NotNull(message = "내용은 필수입니다.")
        String content,
        List<String> addedFileKeys,         //추가된 파일 목록
        List<Long> deletedNoticeFileIds     //삭제된 파일 목록
) {
}
