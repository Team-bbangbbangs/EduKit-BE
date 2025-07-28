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
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncryptor passwordEncryptor;

    @Transactional
    public MemberSignUpResponse signUp(final String email, final String password, final String subjectName,
                                       final String nickname, final School school) {
        authService.validateCondition(email, nickname);
        Subject subject = subjectService.getSubjectByName(subjectName);
        Member member = memberService.createMember(email, password, subject, nickname, school);

        AuthToken authToken = jwtTokenService.generateTokens(member.getMemberUuid());
        // refreshToken을 Redis에 저장하는 로직 구현

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
        // refreshToken을 Redis에 저장하는 로직 구현
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
        // refreshToken Redis 삭제
    }

    @Transactional
    public MemberReissueResponse reissue(final String refreshToken) {
        String memberUuid = jwtTokenService.parseMemberUuidFromRefreshToken(refreshToken);
        Member member = memberService.getMemberByUuid(memberUuid);
        String storedRefreshToken = "";     //redis에서 refreshToken 조회

        if (!jwtTokenService.isTokenEqual(refreshToken, storedRefreshToken)) {
            logout(member.getId());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        AuthToken authToken = jwtTokenService.generateTokens(memberUuid);
        // refreshToken을 Redis에 저장

        return MemberReissueResponse.of(authToken.accessToken(), authToken.refreshToken(), member.isAdmin());
    }
}
