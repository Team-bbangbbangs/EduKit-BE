package com.edukit.core.member.facade;

import com.edukit.core.member.entity.Member;
import com.edukit.core.member.enums.School;
import com.edukit.core.member.facade.response.MemberNicknameValidationResponse;
import com.edukit.core.member.facade.response.MemberProfileGetResponse;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.subject.entity.Subject;
import com.edukit.core.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;
    private final SubjectService subjectService;

    @Transactional(readOnly = true)
    public MemberProfileGetResponse getMemberProfile(final long memberId) {
        Member member = memberService.getMemberById(memberId);
        return MemberProfileGetResponse.of(
                member.getEmail(), member.getSubject().getName(), member.isVerifyTeacher(),
                member.getSchool().getName(), member.getNickname()
        );
    }

    @Transactional
    public void updateMemberProfile(final long memberId, final String subjectName, final School school,
                                    final String nickname) {
        Member member = memberService.getMemberById(memberId);
        Subject subject = subjectService.getSubjectByName(subjectName);
        memberService.updateMemberProfile(member, subject, school, nickname);
    }

    public MemberNicknameValidationResponse validateNickname(final long memberId, final String nickname) {
        Member member = memberService.getMemberById(memberId);
        return MemberNicknameValidationResponse.of(
                memberService.isNicknameInvalid(nickname),
                memberService.isNicknameDuplicated(member, nickname)
        );
    }
}
