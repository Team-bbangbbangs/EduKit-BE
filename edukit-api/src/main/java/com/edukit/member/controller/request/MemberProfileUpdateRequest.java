package com.edukit.member.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record MemberProfileUpdateRequest(
        @Schema(description = "담당 과목", example = "영어")
        @NotNull(message = "과목은 필수 입력값입니다.")
        String subject,
        
        @Schema(description = "학교 종류", example = "middle", allowableValues = {"middle", "high"})
        @NotNull(message = "학교는 필수 입력값입니다.")
        String school,
        
        @Schema(description = "사용자 닉네임", example = "이선생")
        @NotNull(message = "닉네임은 필수 입력값입니다.")
        String nickname
) {
}
