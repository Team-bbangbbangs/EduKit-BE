package com.edukit.core.member.facade;

import com.edukit.core.member.entity.Member;
import com.edukit.core.member.facade.response.MemberProfileGetResponse;
import com.edukit.core.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;

    @Transactional(readOnly = true)
    public MemberProfileGetResponse getMemberProfile(final long memberId) {
        Member member = memberService.getMemberById(memberId);
        return MemberProfileGetResponse.of(
                member.getEmail(), member.getSubject().getName(), member.isVerifyTeacher(),
                member.getSchool().getName(), member.getNickname()
        );
    }
}
