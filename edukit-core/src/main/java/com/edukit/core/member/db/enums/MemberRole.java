package com.edukit.core.member.db.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    TEACHER("ROLE_TEACHER"),
    PENDING_TEACHER("ROLE_PENDING_TEACHER"),
    ADMIN("ROLE_ADMIN");

    private final String role;
}
