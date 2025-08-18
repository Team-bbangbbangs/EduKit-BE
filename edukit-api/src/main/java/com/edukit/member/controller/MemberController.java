package com.edukit.member.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.core.member.db.enums.School;
import com.edukit.member.controller.request.MemberEmailUpdateRequest;
import com.edukit.member.controller.request.MemberProfileUpdateRequest;
import com.edukit.member.controller.request.PasswordChangeRequest;
import com.edukit.member.facade.MemberFacade;
import com.edukit.member.facade.response.MemberProfileGetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class MemberController implements MemberApi {

    private final MemberFacade memberFacade;

    @GetMapping("/profile")
    public ResponseEntity<EdukitResponse<MemberProfileGetResponse>> getMemberProfile(@MemberId final long memberId) {
        return ResponseEntity.ok().body(EdukitResponse.success(memberFacade.getMemberProfile(memberId)));
    }

    @PatchMapping("/profile")
    public ResponseEntity<EdukitResponse<Void>> updateMemberProfile(
            @MemberId final long memberId,
            @RequestBody @Valid final MemberProfileUpdateRequest request
    ) {
        School school = School.fromName(request.school());
        memberFacade.updateMemberProfile(memberId, request.subject(), school, request.nickname());
        return ResponseEntity.ok().body(EdukitResponse.success());
    }

    @GetMapping("/nickname")
    public ResponseEntity<EdukitResponse<Void>> validateNickname(
            @MemberId final long memberId,
            @RequestParam final String nickname
    ) {
        memberFacade.validateNickname(memberId, nickname);
        return ResponseEntity.ok().body(EdukitResponse.success());
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<EdukitResponse<Void>> withdraw(@MemberId final long memberId) {
        memberFacade.withdraw(memberId);
        return ResponseEntity.ok().body(EdukitResponse.success());
    }

    @PatchMapping("/email")
    public ResponseEntity<EdukitResponse<Void>> updateEmail(@MemberId final long memberId,
                                                            @RequestBody @Valid final MemberEmailUpdateRequest request
    ) {
        memberFacade.updateEmail(memberId, request.email());
        return ResponseEntity.ok().body(EdukitResponse.success());
    }

    @PatchMapping("/password")
    public ResponseEntity<EdukitResponse<Void>> updatePassword(@MemberId final long memberId,
                                                               @RequestBody @Valid final PasswordChangeRequest request) {
        memberFacade.updatePassword(memberId, request.currentPassword(), request.newPassword(),
                request.confirmPassword());
        return ResponseEntity.ok().body(EdukitResponse.success());
    }
}
