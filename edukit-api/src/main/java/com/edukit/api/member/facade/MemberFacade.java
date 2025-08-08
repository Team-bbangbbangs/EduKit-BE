package com.edukit.api.member.facade;

import com.edukit.core.auth.service.RefreshTokenStoreService;
import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.db.enums.School;
import com.edukit.api.member.facade.response.MemberNicknameValidationResponse;
import com.edukit.api.member.facade.response.MemberProfileGetResponse;
import com.edukit.core.member.exception.MemberErrorCode;
import com.edukit.core.member.exception.MemberException;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.subject.db.entity.Subject;
import com.edukit.core.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;
    private final SubjectService subjectService;
    private final RefreshTokenStoreService refreshTokenStoreService;

    @Transactional(readOnly = true)
    public MemberProfileGetResponse getMemberProfile(final long memberId) {
        Member member = memberService.getMemberWithSubjectById(memberId);
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
        try {
            memberService.updateMemberProfileAndFlush(member, subject, school, nickname);
        } catch (DataIntegrityViolationException e) {
            throw new MemberException(MemberErrorCode.DUPLICATED_NICKNAME);
        }
    }

    public MemberNicknameValidationResponse validateNickname(final long memberId, final String nickname) {
        Member member = memberService.getMemberById(memberId);
        return MemberNicknameValidationResponse.of(
                memberService.isNicknameInvalid(nickname),
                memberService.isNicknameDuplicated(member, nickname)
        );
    }

    @Transactional
    public void withdraw(final long memberId) {
        Member member = memberService.getMemberById(memberId);
        memberService.withdraw(member);
        refreshTokenStoreService.delete(member.getMemberUuid());
    }
}
