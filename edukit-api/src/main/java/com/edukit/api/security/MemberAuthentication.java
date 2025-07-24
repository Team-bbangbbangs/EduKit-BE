package com.edukit.api.security;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class MemberAuthentication extends UsernamePasswordAuthenticationToken {

    private MemberAuthentication(final Object principal, final Object credentials,
                                 final Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public static MemberAuthentication create(final String memberUuid) {
        return new MemberAuthentication(memberUuid, null, null);
    }
}
