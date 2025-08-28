package com.edukit.auth.facade;

import com.edukit.auth.event.EmailSendEvent;
import com.edukit.auth.event.MemberSignedUpEvent;
import com.edukit.auth.event.PasswordFindEvent;
import com.edukit.auth.facade.response.MemberLoginResponse;
import com.edukit.auth.facade.response.MemberReissueResponse;
import com.edukit.auth.facade.response.MemberSignUpResponse;
import com.edukit.core.auth.db.enums.VerificationCodeType;
import com.edukit.core.auth.exception.AuthErrorCode;
import com.edukit.core.auth.exception.AuthException;
import com.edukit.core.auth.service.AuthService;
import com.edukit.core.auth.service.JwtTokenService;
import com.edukit.core.auth.service.RefreshTokenStoreService;
import com.edukit.core.auth.service.VerificationCodeService;
import com.edukit.core.auth.service.jwt.dto.AuthToken;
import com.edukit.core.auth.util.PasswordValidator;
import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.db.enums.School;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.subject.db.entity.Subject;
import com.edukit.core.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthService authService;
    private final MemberService memberService;
    private final SubjectService subjectService;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;
    private final RefreshTokenStoreService refreshTokenStoreService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MemberSignUpResponse signUp(final String email, final String password, final String subjectName,
                                       final String nickname, final School school) {
        authService.validateCondition(password, email);
        memberService.validateNickname(nickname);
        Subject subject = subjectService.getSubjectByName(subjectName);
        String encodedPassword = passwordEncoder.encode(password);

        Member member = memberService.createMember(email, encodedPassword, subject, nickname, school);

        AuthToken authToken = jwtTokenService.generateTokens(member.getMemberUuid());
        refreshTokenStoreService.store(member.getMemberUuid(), authToken.refreshToken());

        String verificationCode = verificationCodeService.issueVerificationCode(member,
                VerificationCodeType.TEACHER_VERIFICATION);

        eventPublisher.publishEvent(
                MemberSignedUpEvent.of(member.getEmail(), member.getMemberUuid(), verificationCode));
        return MemberSignUpResponse.of(authToken.accessToken(), authToken.refreshToken());
    }

    public MemberLoginResponse login(final String email, final String password) {
        Member member = memberService.getMemberByEmail(email);

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }

        AuthToken authToken = jwtTokenService.generateTokens(member.getMemberUuid());
        refreshTokenStoreService.store(member.getMemberUuid(), authToken.refreshToken());

        return MemberLoginResponse.of(authToken.accessToken(), authToken.refreshToken(), member.isAdmin());
    }

    public void logout(final long memberId) {
        Member member = memberService.getMemberById(memberId);
        refreshTokenStoreService.delete(member.getMemberUuid());
    }

    @Transactional(readOnly = true)
    public MemberReissueResponse reissue(final String refreshToken) {
        String memberUuid = jwtTokenService.parseMemberUuidFromRefreshToken(refreshToken);
        Member member = memberService.getMemberByUuid(memberUuid);

        String storedRefreshToken = refreshTokenStoreService.get(memberUuid);
        if (!jwtTokenService.isTokenEqual(refreshToken, storedRefreshToken)) {
            logout(member.getId());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        AuthToken authToken = jwtTokenService.generateTokens(memberUuid);
        refreshTokenStoreService.store(memberUuid, authToken.refreshToken());

        return MemberReissueResponse.of(authToken.accessToken(), authToken.refreshToken(), member.isAdmin());
    }

    @Transactional
    public void updatePassword(final String memberUuid, final String verificationCode, final String newPassword,
                               final String confirmPassword) {
        PasswordValidator.validatePasswordFormat(newPassword);
        PasswordValidator.validatePasswordEquality(newPassword, confirmPassword);

        Member member = memberService.getMemberByUuid(memberUuid);
        verificationCodeService.checkVerificationCode(member, verificationCode, VerificationCodeType.PASSWORD_RESET);

        if (passwordEncoder.matches(newPassword, member.getPassword())) {
            throw new AuthException(AuthErrorCode.SAME_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        memberService.updatePassword(member, encodedPassword);
    }

    @Transactional
    public void sendVerificationEmail(final long memberId) {
        Member member = memberService.getMemberById(memberId);
        String verificationCode = verificationCodeService.issueVerificationCode(member,
                VerificationCodeType.TEACHER_VERIFICATION);
        eventPublisher.publishEvent(
                EmailSendEvent.of(member.getEmail(), member.getMemberUuid(), verificationCode));
    }

    @Transactional
    public void verifyEmailCode(final String memberUuid, final String verificationCode) {
        Member member = memberService.getMemberByUuid(memberUuid);
        verificationCodeService.checkVerificationCode(member, verificationCode,
                VerificationCodeType.TEACHER_VERIFICATION);
        memberService.memberVerified(member);
    }

    @Transactional
    public void findPassword(final String email) {
        Member member = memberService.getMemberByEmail(email);
        String verificationCode = verificationCodeService.issueVerificationCode(member,
                VerificationCodeType.PASSWORD_RESET);
        eventPublisher.publishEvent(
                PasswordFindEvent.of(member.getEmail(), member.getMemberUuid(), verificationCode));
    }

    public void validateNickname(final String nickname) {
        memberService.validateNickname(nickname);
    }
}
