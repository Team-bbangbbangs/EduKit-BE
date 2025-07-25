package com.edukit.core.auth.facade;

import com.edukit.core.auth.enums.AuthCodeType;
import com.edukit.core.auth.event.MemberSignedUpEvent;
import com.edukit.core.auth.facade.response.MemberSignUpResponse;
import com.edukit.core.auth.service.AuthCodeService;
import com.edukit.core.auth.service.AuthService;
import com.edukit.core.auth.service.Token;
import com.edukit.core.auth.service.TokenService;
import com.edukit.core.member.entity.Member;
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
    private final AuthCodeService authCodeService;
    private final MemberService memberService;
    private final SubjectService subjectService;
    private final TokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MemberSignUpResponse signUp(final String email, final String password, final String subjectName,
                                       final String nickname, final String school) {
        authService.validateCondition(email, nickname);
        Subject subject = subjectService.getSubjectByName(subjectName);
        Member member = memberService.createMember(email, password, subject, nickname, school);

        Token token = tokenService.generateTokens(member.getMemberUuid());
        // refreshToken을 Redis에 저장하는 로직 구현

        String authCode = authCodeService.issueVerificationCode(member, AuthCodeType.TEACHER_VERIFICATION);

        eventPublisher.publishEvent(MemberSignedUpEvent.of(member.getEmail(), member.getMemberUuid(), authCode));
        return MemberSignUpResponse.of(token.accessToken(), token.refreshToken());
    }
}
