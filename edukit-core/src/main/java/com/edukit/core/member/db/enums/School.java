package com.edukit.core.member.db.enums;

import com.edukit.core.member.exception.MemberException;
import com.edukit.core.member.exception.MemberErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum School {
    MIDDLE_SCHOOL("middle"),
    HIGH_SCHOOL("high");

    private final String name;

    public static School fromName(final String name) {
        return Arrays.stream(School.values())
                .filter(school -> school.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_SCHOOL_TYPE));
    }
}
