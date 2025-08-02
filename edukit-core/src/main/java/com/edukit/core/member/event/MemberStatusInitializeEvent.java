package com.edukit.core.member.event;

import com.edukit.core.auth.service.dto.MemberVerificationData;
import java.util.List;

public record MemberStatusInitializeEvent(
        List<MemberVerificationData> memberVerificationData
) {
    public static MemberStatusInitializeEvent of(final List<MemberVerificationData> memberVerificationData) {
        return new MemberStatusInitializeEvent(memberVerificationData);
    }
}
