package com.edukit.member.facade.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberProfileGetResponse(
        @Schema(description = "사용자 이메일", example = "teacher@sen.go.kr")
        String email,
        
        @Schema(description = "담당 과목", example = "수학")
        String subject,
        
        @Schema(description = "교사 인증 여부", example = "true")
        boolean isTeacherVerified,
        
        @Schema(description = "학교 종류", example = "high")
        String school,
        
        @Schema(description = "사용자 닉네임", example = "김선생")
        String nickname
) {
    public static MemberProfileGetResponse of(final String email,
                                              final String subject,
                                              final boolean isTeacherVerified,
                                              final String school,
                                              final String nickname) {
        return new MemberProfileGetResponse(email, subject, isTeacherVerified, school, nickname);
    }
}
