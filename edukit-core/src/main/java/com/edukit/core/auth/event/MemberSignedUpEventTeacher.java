package com.edukit.core.auth.event;

public record MemberSignedUpEventTeacher(
        String email,
        String memberUuid,
        String verificationCode
) implements TeacherVerificationEmailEvent {

    public static MemberSignedUpEventTeacher of(final String email, final String memberUuid, final String verificationCode) {
        return new MemberSignedUpEventTeacher(email, memberUuid, verificationCode);
    }
}
