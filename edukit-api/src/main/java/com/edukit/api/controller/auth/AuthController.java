package com.edukit.api.controller.auth;

import com.edukit.api.common.ApiResponse;
import com.edukit.api.controller.auth.request.MemberSignUpRequest;
import com.edukit.api.security.handler.RefreshTokenCookieHandler;
import com.edukit.api.security.util.PasswordValidator;
import com.edukit.common.exception.code.CommonSuccessCode;
import com.edukit.core.auth.facade.AuthFacade;
import com.edukit.core.auth.facade.response.MemberSignUpResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<MemberSignUpResponse> signUp(@RequestBody @Valid final MemberSignUpRequest request,
                                                    final HttpServletResponse servletResponse) {
        PasswordValidator.validatePasswordFormat(request.password());
        String encodedPassword = passwordEncoder.encode(request.password());
        MemberSignUpResponse response = authFacade.signUp(request.email(), encodedPassword, request.subject(),
                request.nickname(), request.school());

        servletResponse.addCookie(cookieHandler.createRefreshTokenCookie(response.refreshToken()));
        return ApiResponse.success(CommonSuccessCode.OK, response);
    }
}
