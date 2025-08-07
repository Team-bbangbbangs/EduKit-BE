package com.edukit.api.auth.controller;

import static com.edukit.api.common.security.handler.RefreshTokenCookieHandler.REFRESH_TOKEN_COOKIE_NAME;

import com.edukit.api.auth.controller.request.MemberLoginRequest;
import com.edukit.api.auth.controller.request.MemberSignUpRequest;
import com.edukit.api.auth.controller.request.UpdatePasswordRequest;
import com.edukit.api.auth.facade.AuthFacade;
import com.edukit.api.auth.facade.response.MemberLoginResponse;
import com.edukit.api.auth.facade.response.MemberReissueResponse;
import com.edukit.api.auth.facade.response.MemberSignUpResponse;
import com.edukit.api.common.EdukitResponse;
import com.edukit.api.common.annotation.MemberId;
import com.edukit.api.common.security.handler.RefreshTokenCookieHandler;
import com.edukit.core.member.db.enums.School;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;
    private final RefreshTokenCookieHandler cookieHandler;

    @PostMapping("/signup")
    public ResponseEntity<EdukitResponse<MemberSignUpResponse>> signUp(
            @RequestBody @Valid final MemberSignUpRequest request) {

        School school = School.fromName(request.school());

        MemberSignUpResponse response = authFacade.signUp(request.email(), request.password(), request.subject(),
                request.nickname(), school);

        ResponseCookie refreshCookie = cookieHandler.createRefreshTokenCookie(response.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(EdukitResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<EdukitResponse<MemberLoginResponse>> login(
            @RequestBody @Valid final MemberLoginRequest request) {
        MemberLoginResponse loginResponse = authFacade.login(request.email(), request.password());

        ResponseCookie refreshTokenCookie = cookieHandler.createRefreshTokenCookie(loginResponse.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(EdukitResponse.success(loginResponse));

    }

    @PostMapping("/logout")
    public ResponseEntity<EdukitResponse<Void>> logout(@MemberId final long memberId) {
        authFacade.logout(memberId);

        ResponseCookie clearedRefreshTokenCookie = cookieHandler.createClearedRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearedRefreshTokenCookie.toString())
                .body(EdukitResponse.success());
    }

    @PatchMapping("/reissue")
    public ResponseEntity<EdukitResponse<MemberReissueResponse>> reissue(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME) final String refreshToken) {
        MemberReissueResponse reissueResponse = authFacade.reissue(refreshToken.strip());

        ResponseCookie refreshTokenCookie = cookieHandler.createRefreshTokenCookie(reissueResponse.refreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(EdukitResponse.success(reissueResponse));
    }

    @PatchMapping("/password")
    public ResponseEntity<EdukitResponse<Void>> updatePassword(
            @RequestBody @Valid final UpdatePasswordRequest request) {
        authFacade.updatePassword(request.memberUuid(), request.verificationCode(), request.password(),
                request.confirmPassword());
        return ResponseEntity.ok(EdukitResponse.success());
    }
}
