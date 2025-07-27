package com.edukit.core.auth.facade;

import com.edukit.core.auth.enums.AuthCodeType;
import com.edukit.core.auth.event.MemberSignedUpEvent;
import com.edukit.core.auth.facade.response.MemberSignUpResponse;
import com.edukit.core.auth.jwt.dto.AuthToken;
import com.edukit.core.auth.service.AuthService;
import com.edukit.core.auth.service.JwtTokenService;
import com.edukit.core.auth.service.VerificationCodeService;
import com.edukit.core.member.entity.Member;
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

    @Transactional
    public MemberSignUpResponse signUp(final String email, final String password, final String subjectName,
                                       final String nickname, final School school) {
        authService.validateCondition(email, nickname);
        Subject subject = subjectService.getSubjectByName(subjectName);
        Member member = memberService.createMember(email, password, subject, nickname, school);

        AuthToken authToken = jwtTokenService.generateTokens(member.getMemberUuid());
        // refreshToken을 Redis에 저장하는 로직 구현

        String verificationCode = verificationCodeService.issueVerificationCode(member,
                AuthCodeType.TEACHER_VERIFICATION);

        eventPublisher.publishEvent(
                MemberSignedUpEvent.of(member.getEmail(), member.getMemberUuid(), verificationCode));
        return MemberSignUpResponse.of(authToken.accessToken(), authToken.refreshToken());
    }
}
