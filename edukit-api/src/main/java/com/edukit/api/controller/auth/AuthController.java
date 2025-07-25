package com.edukit.api.controller.auth;

import com.edukit.api.common.ApiResponse;
import com.edukit.api.controller.auth.request.MemberSignUpRequest;
import com.edukit.api.controller.auth.response.MemberSignUpResponse;
import com.edukit.api.security.jwt.service.JwtGenerator;
import com.edukit.api.security.jwt.service.Token;
import com.edukit.common.exception.code.CommonSuccessCode;
import com.edukit.core.auth.facade.AuthFacade;
import com.edukit.core.auth.facade.dto.SignUpResult;
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

    private final PasswordEncoder passwordEncoder;
    private final AuthFacade authFacade;
    private final JwtGenerator jwtGenerator;

    @PostMapping("/signup")
    public ApiResponse<MemberSignUpResponse> signUp(@RequestBody @Valid final MemberSignUpRequest request) {
        String encodedPassword = passwordEncoder.encode(request.password());
        SignUpResult result = authFacade.signUp(request.email(), encodedPassword, request.subject(), request.nickname(),
                request.school());

        Token tokens = jwtGenerator.generateTokens(result.memberUuid());
        MemberSignUpResponse response = MemberSignUpResponse.of(tokens.accessToken());
        return ApiResponse.success(CommonSuccessCode.OK, response);
    }
}
