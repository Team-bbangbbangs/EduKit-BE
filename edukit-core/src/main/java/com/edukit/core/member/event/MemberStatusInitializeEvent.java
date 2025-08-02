package com.edukit.core.member.event;

import com.edukit.core.member.entity.Member;
import java.util.List;

public record MemberStatusInitializeEvent(
        List<Member> members
) {
    public static MemberStatusInitializeEvent of(final List<Member> members) {
        return new MemberStatusInitializeEvent(members);
    }
}
