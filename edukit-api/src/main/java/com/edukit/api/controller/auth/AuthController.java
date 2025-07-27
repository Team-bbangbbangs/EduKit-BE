package com.edukit.api.controller.auth;

import com.edukit.api.common.EdukitResponse;
import com.edukit.api.controller.auth.request.MemberSignUpRequest;
import com.edukit.api.security.handler.RefreshTokenCookieHandler;
import com.edukit.api.security.util.PasswordValidator;
import com.edukit.core.auth.facade.AuthFacade;
import com.edukit.core.auth.facade.response.MemberSignUpResponse;
import com.edukit.core.member.enums.School;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenCookieHandler cookieHandler;

    @PostMapping("/signup")
    public ResponseEntity<EdukitResponse<MemberSignUpResponse>> signUp(
            @RequestBody @Valid final MemberSignUpRequest request) {

        PasswordValidator.validatePasswordFormat(request.password());
        String encodedPassword = passwordEncoder.encode(request.password());
        School school = School.fromName(request.school());

        MemberSignUpResponse response = authFacade.signUp(request.email(), encodedPassword, request.subject(),
                request.nickname(), school);

        ResponseCookie refreshCookie = cookieHandler.createRefreshTokenCookie(response.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(EdukitResponse.success(response));
    }
}
