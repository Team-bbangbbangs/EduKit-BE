package com.edukit.core.auth.event;

import java.util.Map;
import org.slf4j.MDC;

public record MemberSignedUpEvent(
        String email,
        String memberUuid,
        String verificationCode,
        Map<String, String> mdcContext
) {
    public static MemberSignedUpEvent of(final String email, final String memberUuid, final String verificationCode) {
        return new MemberSignedUpEvent(email, memberUuid, verificationCode, MDC.getCopyOfContextMap());
    }
}
