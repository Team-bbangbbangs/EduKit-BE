package com.edukit.api.security.authentication;

import com.edukit.core.member.entity.Member;
import com.edukit.core.member.enums.MemberRole;
import com.edukit.core.member.service.MemberService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDetailReader implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(final String memberUuid) {
        Member member = memberService.getMemberByUuid(memberUuid);
        String memberId = String.valueOf(member.getId());
        MemberRole memberRole = member.getRole();
        Collection<? extends GrantedAuthority> authorities = MemberRoleConverter.toGrantedAuthorities(memberRole);

        return new User(memberId, "password", authorities);
    }
}
