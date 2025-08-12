package com.edukit.member.facade;

import com.edukit.core.auth.db.enums.VerificationCodeType;
import com.edukit.core.auth.service.AuthService;
import com.edukit.core.auth.service.RefreshTokenStoreService;
import com.edukit.core.auth.service.VerificationCodeService;
import com.edukit.core.auth.util.PasswordValidator;
import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.db.enums.School;
import com.edukit.core.member.event.MemberEmailUpdateEvent;
import com.edukit.core.member.exception.MemberErrorCode;
import com.edukit.core.member.exception.MemberException;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.subject.db.entity.Subject;
import com.edukit.core.subject.service.SubjectService;
import com.edukit.member.facade.response.MemberNicknameValidationResponse;
import com.edukit.member.facade.response.MemberProfileGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;
    private final AuthService authService;
    private final SubjectService subjectService;
    private final RefreshTokenStoreService refreshTokenStoreService;
    private final VerificationCodeService verificationCodeService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

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
                memberService.isNicknameDuplicated(nickname, member)
        );
    }

    @Transactional
    public void withdraw(final long memberId) {
        Member member = memberService.getMemberById(memberId);
        memberService.withdraw(member);
        refreshTokenStoreService.delete(member.getMemberUuid());
    }

    @Transactional
    public void updateEmail(final long memberId, final String email) {
        authService.validateEmail(email);
        Member member = memberService.getMemberById(memberId);
        memberService.updateEmail(member, email);
        String verificationCode = verificationCodeService.issueVerificationCode(member,
                VerificationCodeType.TEACHER_VERIFICATION);
        eventPublisher.publishEvent(
                MemberEmailUpdateEvent.of(member.getEmail(), member.getMemberUuid(), verificationCode));
    }

    @Transactional
    public void updatePassword(final long memberId, final String currentPassword, final String newPassword) {
        Member member = memberService.getMemberById(memberId);
        PasswordValidator.validatePasswordFormat(newPassword);
        validatePassword(member, currentPassword, newPassword);
        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    private void validatePassword(final Member member, final String currentPassword, final String newPassword) {
        String savedPassword = member.getPassword();
        if (!isPasswordMatched(currentPassword, savedPassword)) {
            throw new MemberException(MemberErrorCode.INVALID_CURRENT_PASSWORD);
        }
        if (isPasswordMatched(newPassword, savedPassword)) {
            throw new MemberException(MemberErrorCode.SAME_PASSWORD);
        }
    }

    private boolean isPasswordMatched(final String inputPassword, final String savedPassword) {
        return passwordEncoder.matches(inputPassword, savedPassword);
    }
}
