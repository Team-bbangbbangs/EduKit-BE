package com.edukit.core.auth.facade;

import com.edukit.core.auth.enums.VerificationCodeType;
import com.edukit.core.auth.event.MemberSignedUpEvent;
import com.edukit.core.auth.exception.AuthErrorCode;
import com.edukit.core.auth.exception.AuthException;
import com.edukit.core.auth.facade.response.MemberLoginResponse;
import com.edukit.core.auth.facade.response.MemberReissueResponse;
import com.edukit.core.auth.facade.response.MemberSignUpResponse;
import com.edukit.core.auth.jwt.dto.AuthToken;
import com.edukit.core.auth.service.AuthService;
import com.edukit.core.auth.service.JwtTokenService;
import com.edukit.core.auth.service.RefreshTokenStoreService;
import com.edukit.core.auth.service.VerificationCodeService;
import com.edukit.core.auth.util.PasswordEncryptor;
import com.edukit.core.member.entity.Member;
import com.edukit.core.member.enums.MemberRole;
import com.edukit.core.member.enums.School;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.subject.entity.Subject;
import com.edukit.core.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthService authService;
    private final MemberService memberService;
    private final SubjectService subjectService;
    private final JwtTokenService jwtTokenService;
    private final VerificationCodeService verificationCodeService;
    private final RefreshTokenStoreService refreshTokenStoreService;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncryptor passwordEncryptor;

    @Transactional
    public MemberSignUpResponse signUp(final String email, final String password, final String subjectName,
                                       final String nickname, final School school) {
        authService.validateCondition(email, nickname);
        Subject subject = subjectService.getSubjectByName(subjectName);
        Member member = memberService.createMember(email, password, subject, nickname, school);

        AuthToken authToken = jwtTokenService.generateTokens(member.getMemberUuid());
        refreshTokenStoreService.save(member.getMemberUuid(), authToken.refreshToken());

        String verificationCode = verificationCodeService.issueVerificationCode(member,
                VerificationCodeType.TEACHER_VERIFICATION);

        eventPublisher.publishEvent(
                MemberSignedUpEvent.of(member.getEmail(), member.getMemberUuid(), verificationCode));
        return MemberSignUpResponse.of(authToken.accessToken(), authToken.refreshToken());
    }

    @Transactional(readOnly = true)
    public void checkHasPermission(final long memberId) {
        Member member = memberService.getMemberById(memberId);
        if (member.getRole() == MemberRole.PENDING_TEACHER) {
            throw new AuthException(AuthErrorCode.FORBIDDEN_MEMBER);
        }
    }

    public MemberLoginResponse login(final String email, final String password) {
        Member member = memberService.getMemberByEmail(email);
        if (!passwordEncryptor.matches(password, member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }

        AuthToken authToken = jwtTokenService.generateTokens(member.getMemberUuid());
        refreshTokenStoreService.save(member.getMemberUuid(), authToken.refreshToken());

        return MemberLoginResponse.of(authToken.accessToken(), authToken.refreshToken(), member.isAdmin());
    }

    @Transactional
    public void withdraw(final long memberId) {
        Member member = memberService.getMemberById(memberId);
        memberService.withdraw(member);
        //TODO refresh token 삭제
    }

    public void logout(final long memberId) {
        Member member = memberService.getMemberById(memberId);
        refreshTokenStoreService.delete(member.getMemberUuid());
    }

    @Transactional
    public MemberReissueResponse reissue(final String refreshToken) {
        String memberUuid = jwtTokenService.parseMemberUuidFromRefreshToken(refreshToken);
        Member member = memberService.getMemberByUuid(memberUuid);
        String storedRefreshToken = refreshTokenStoreService.get(memberUuid);
        if (!jwtTokenService.isTokenEqual(refreshToken, storedRefreshToken)) {
            logout(member.getId());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        AuthToken authToken = jwtTokenService.generateTokens(memberUuid);
        refreshTokenStoreService.save(memberUuid, authToken.refreshToken());

        return MemberReissueResponse.of(authToken.accessToken(), authToken.refreshToken(), member.isAdmin());
    }

    @Transactional
    public void updatePassword(final String verificationCode, final String email, final String password,
                               final String confirmPassword) {
        Member member = memberService.getMemberByEmail(email);
        verificationCodeService.verifyPasswordResetCode(member, verificationCode);
        if (!password.equals(confirmPassword)) {
            throw new AuthException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }
        if (passwordEncryptor.matches(password, member.getPassword())) {
            throw new AuthException(AuthErrorCode.SAME_PASSWORD);
        }

        memberService.updatePassword(member, passwordEncryptor.encode(password));
    }
}
