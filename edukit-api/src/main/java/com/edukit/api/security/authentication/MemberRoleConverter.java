package com.edukit.api.security.authentication;

import com.edukit.core.member.db.enums.MemberRole;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class MemberRoleConverter {

    public static Collection<? extends GrantedAuthority> toGrantedAuthorities(final MemberRole memberRole) {
        return List.of(new SimpleGrantedAuthority(memberRole.getRole()));
    }
}
