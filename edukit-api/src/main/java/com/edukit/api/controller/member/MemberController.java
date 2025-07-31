package com.edukit.api.controller.member;

import com.edukit.api.common.EdukitResponse;
import com.edukit.api.common.annotation.MemberId;
import com.edukit.core.member.facade.MemberFacade;
import com.edukit.core.member.facade.response.MemberProfileGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberFacade memberFacade;

    @GetMapping("/profile")
    public ResponseEntity<EdukitResponse<MemberProfileGetResponse>> getMemberProfile(@MemberId final long memberId) {
        return ResponseEntity.ok().body(EdukitResponse.success(memberFacade.getMemberProfile(memberId)));
    }
}
