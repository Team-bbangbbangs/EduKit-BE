package com.edukit.member.controller;

import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.member.controller.request.MemberEmailUpdateRequest;
import com.edukit.member.controller.request.MemberProfileUpdateRequest;
import com.edukit.member.controller.request.PasswordChangeRequest;
import com.edukit.member.facade.response.MemberProfileGetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "회원 관리", description = "회원 프로필 및 계정 관리 API")
public interface MemberApi {

    @Operation(
            summary = "회원 프로필 조회"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            description = "Authorization 헤더에 토큰이 누락된 경우",
                                            value = """
                                                      {
                                                        "code": "A-40101",
                                                        "message": "토큰이 누락되었습니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            description = "만료되거나 잘못된 토큰인 경우",
                                            value = """
                                                      {
                                                        "code": "A-40102",
                                                        "message": "유효하지 않은 토큰입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "회원 미존재",
                                            description = "토큰은 유효하지만 해당 회원이 존재하지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "M-40401",
                                                        "message": "존재하지 않는 회원입니다. 회원가입을 진행해주세요."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<MemberProfileGetResponse>> getMemberProfile(@MemberId final long memberId);

    @Operation(
            summary = "회원 프로필 수정"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "과목, 학교, 닉네임 중 하나라도 null인 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "subject": "과목은 필수 입력값입니다.",
                                                          "school": "학교는 필수 입력값입니다.",
                                                          "nickname": "닉네임은 필수 입력값입니다."
                                                        }
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 학교 구분",
                                            description = "middle 또는 high가 아닌 값을 입력한 경우",
                                            value = """
                                                      {
                                                        "code": "M-40003",
                                                        "message": "중학교, 고등학교 중 하나를 선택해주세요."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 과목",
                                            description = "시스템에 등록되지 않은 과목명을 입력한 경우",
                                            value = """
                                                      {
                                                        "code": "S-40401",
                                                        "message": "해당 과목이 존재하지 않습니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "중복된 닉네임",
                                            description = "다른 회원이 이미 사용중인 닉네임인 경우",
                                            value = """
                                                      {
                                                        "code": "M-40005",
                                                        "message": "입력하신 닉네임은 중복된 닉네임입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "회원 미존재",
                                            description = "수정하려는 회원이 존재하지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "M-40401",
                                                        "message": "존재하지 않는 회원입니다. 회원가입을 진행해주세요."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> updateMemberProfile(
            @MemberId final long memberId,
            @RequestBody @Valid final MemberProfileUpdateRequest request
    );

    @Operation(
            summary = "닉네임 검증"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "회원 미존재",
                                            description = "검증을 요청한 회원이 존재하지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "M-40401",
                                                        "message": "존재하지 않는 회원입니다. 회원가입을 진행해주세요."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "닉네임 파라미터 누락",
                                            description = "nickname 파라미터가 누락된 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "필수 요청 파라미터가 누락되었습니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 닉네임",
                                            description = "닉네임 형식이 올바르지 않거나 금지어가 포함된 경우",
                                            value = """
                                                      {
                                                        "code": "M-40004",
                                                        "message": "입력하신 닉네임은 유효하지 않습니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "중복된 닉네임",
                                            description = "다른 회원이 이미 사용중인 닉네임인 경우",
                                            value = """
                                                      {
                                                        "code": "M-40005",
                                                        "message": "입력하신 닉네임은 중복된 닉네임입니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> validateNickname(
            @MemberId final long memberId,
            @RequestParam final String nickname
    );

    @Operation(
            summary = "회원 탈퇴"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "회원 미존재",
                                            description = "탈퇴하려는 회원이 존재하지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "M-40401",
                                                        "message": "존재하지 않는 회원입니다. 회원가입을 진행해주세요."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> withdraw(@MemberId final long memberId);

    @Operation(
            summary = "이메일 주소 변경"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "이메일이 null인 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "email": "이메일은 필수 입력값입니다."
                                                        }
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이메일 형식 오류",
                                            description = "유효하지 않은 이메일 형식인 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "email": "이메일 형식이 올바르지 않습니다."
                                                        }
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 교사 이메일",
                                            description = "교육청 도메인이 아닌 이메일인 경우",
                                            value = """
                                                      {
                                                        "code": "A-40005",
                                                        "message": "유효하지 않은 교사 이메일입니다. 교육청 이메일 도메인만 허용됩니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "회원 미존재",
                                            description = "이메일을 변경하려는 회원이 존재하지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "M-40401",
                                                        "message": "존재하지 않는 회원입니다. 회원가입을 진행해주세요."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> updateEmail(
            @MemberId final long memberId,
            @RequestBody @Valid final MemberEmailUpdateRequest request
    );

    @Operation(
            summary = "비밀번호 변경"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "비밀번호 필드 중 하나라도 null인 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "currentPassword": "현재 비밀번호를 입력해주세요.",
                                                          "newPassword": "새로운 비밀번호를 입력해주세요.",
                                                          "confirmPassword": "비밀번호 확인을 입력해주세요."
                                                        }
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "현재 비밀번호 불일치",
                                            description = "입력한 현재 비밀번호가 틀린 경우",
                                            value = """
                                                      {
                                                        "code": "M-40006",
                                                        "message": "현재 비밀번호가 일치하지 않습니다. 다시 입력해주세요."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "새 비밀번호 형식 오류",
                                            description = "새 비밀번호가 규칙에 맞지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "A-40007",
                                                        "message": "유효하지 않은 비밀번호 양식입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "비밀번호 확인 불일치",
                                            description = "새 비밀번호와 확인 비밀번호가 다른 경우",
                                            value = """
                                                      {
                                                        "code": "A-40010",
                                                        "message": "새 비밀번호와 비밀번호 확인이 일치하지 않습니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "기존 비밀번호와 동일",
                                            description = "새 비밀번호가 현재 비밀번호와 같은 경우",
                                            value = """
                                                      {
                                                        "code": "M-40007",
                                                        "message": "새로운 비밀번호는 기존 비밀번호와 같을 수 없습니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "회원 미존재",
                                            description = "비밀번호를 변경하려는 회원이 존재하지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "M-40401",
                                                        "message": "존재하지 않는 회원입니다. 회원가입을 진행해주세요."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> updatePassword(
            @MemberId final long memberId,
            @RequestBody @Valid final PasswordChangeRequest request
    );
}
