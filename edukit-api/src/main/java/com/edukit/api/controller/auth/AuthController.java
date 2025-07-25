package com.edukit.api.controller.auth;

import com.edukit.api.common.ApiResponse;
import com.edukit.api.controller.auth.request.MemberSignUpRequest;
import com.edukit.common.exception.code.CommonSuccessCode;
import com.edukit.core.auth.facade.AuthFacade;
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

    @PostMapping("/signup")
    public ApiResponse<Void> signUp(@RequestBody @Valid final MemberSignUpRequest request) {
        String encodedPassword = passwordEncoder.encode(request.password());
        return ApiResponse.success(CommonSuccessCode.OK);
    }
}
