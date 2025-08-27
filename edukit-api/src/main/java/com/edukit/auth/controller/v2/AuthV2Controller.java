package com.edukit.auth.controller.v2;

import com.edukit.auth.controller.request.PasswordFindRequest;
import com.edukit.auth.controller.request.UpdatePasswordRequest;
import com.edukit.auth.facade.AuthFacade;
import com.edukit.common.EdukitResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthV2Controller implements AuthV2Api {

    private final AuthFacade authFacade;

    @PostMapping("/find-password")
    public ResponseEntity<EdukitResponse<Void>> findPassword(@RequestBody @Valid final PasswordFindRequest request) {
        authFacade.findPassword(request.email());
        return ResponseEntity.ok(EdukitResponse.success());
    }

    @PatchMapping("/password")
    public ResponseEntity<EdukitResponse<Void>> updatePassword(
            @RequestBody @Valid final UpdatePasswordRequest request) {
        authFacade.updatePassword(request.memberUuid(), request.verificationCode(), request.password(),
                request.confirmPassword());
        return ResponseEntity.ok(EdukitResponse.success());
    }
}
