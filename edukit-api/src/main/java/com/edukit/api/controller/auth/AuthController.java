package com.edukit.api.controller.auth;

import static com.edukit.api.security.handler.RefreshTokenCookieHandler.REFRESH_TOKEN_COOKIE_NAME;

import com.edukit.api.common.EdukitResponse;
import com.edukit.api.common.annotation.MemberId;
import com.edukit.api.controller.auth.request.MemberLoginRequest;
import com.edukit.api.controller.auth.request.MemberSignUpRequest;
import com.edukit.api.controller.auth.request.UpdatePasswordRequest;
import com.edukit.api.security.handler.RefreshTokenCookieHandler;
import com.edukit.api.security.util.PasswordValidator;
import com.edukit.core.auth.facade.AuthFacade;
import com.edukit.core.auth.facade.response.MemberLoginResponse;
import com.edukit.core.auth.facade.response.MemberReissueResponse;
import com.edukit.core.auth.facade.response.MemberSignUpResponse;
import com.edukit.core.member.enums.School;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping("/login")
    public ResponseEntity<EdukitResponse<MemberLoginResponse>> login(
            @RequestBody @Valid final MemberLoginRequest request) {
        MemberLoginResponse loginResponse = authFacade.login(request.email().strip(), request.password().strip());

        ResponseCookie refreshTokenCookie = cookieHandler.createRefreshTokenCookie(loginResponse.refreshToken());
        log.info("로그인 성공: email={}", request.email().strip());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(EdukitResponse.success(loginResponse));

    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<EdukitResponse<Void>> withdraw(@MemberId final long memberId) {
        authFacade.withdraw(memberId);
        return ResponseEntity.ok().body(EdukitResponse.success());
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
        log.info("토큰 재발급 성공: refreshToken(앞 8자리)={}",
                refreshToken.length() > 8 ? refreshToken.substring(0, 8) : refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(EdukitResponse.success(reissueResponse));
    }

    @PatchMapping("/password")
    public ResponseEntity<EdukitResponse<Void>> updatePassword(
            @RequestBody @Valid final UpdatePasswordRequest request) {
        PasswordValidator.validatePasswordFormat(request.password());

        authFacade.updatePassword(request.verificationCode().strip(), request.email().strip(),
                request.password().strip(), request.confirmPassword().strip());
        return ResponseEntity.ok(EdukitResponse.success());
    }
}
