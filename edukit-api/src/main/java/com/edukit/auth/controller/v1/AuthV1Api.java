package com.edukit.auth.controller.v1;

import com.edukit.auth.controller.request.MemberLoginRequest;
import com.edukit.auth.controller.request.MemberSignUpRequest;
import com.edukit.auth.facade.response.MemberLoginResponse;
import com.edukit.auth.facade.response.MemberReissueResponse;
import com.edukit.auth.facade.response.MemberSignUpResponse;
import com.edukit.common.EdukitResponse;
import com.edukit.common.annotation.MemberId;
import com.edukit.member.facade.response.MemberNicknameValidationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증/인가", description = "회원가입, 로그인, 토큰 관리 및 이메일 인증 API")
public interface AuthV1Api {

    @Operation(
            summary = "회원가입"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "이메일, 비밀번호, 과목, 닉네임, 학교 중 하나라도 null인 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "email": "이메일은 필수 입력값입니다.",
                                                          "password": "비밀번호는 필수 입력값입니다.",
                                                          "subject": "과목은 필수 입력값입니다.",
                                                          "nickname": "닉네임은 필수 입력값입니다.",
                                                          "school": "학교는 필수 입력값입니다."
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
                                            name = "비밀번호 형식 오류",
                                            description = "비밀번호가 규칙에 맞지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "A-40007",
                                                        "message": "유효하지 않은 비밀번호 양식입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 등록된 회원",
                                            description = "동일한 이메일로 이미 가입된 회원이 있는 경우",
                                            value = """
                                                      {
                                                        "code": "A-40906",
                                                        "message": "이미 등록된 회원입니다."
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
                                            name = "유효하지 않은 학교 구분",
                                            description = "middle 또는 high가 아닌 값을 입력한 경우",
                                            value = """
                                                      {
                                                        "code": "M-40003",
                                                        "message": "중학교, 고등학교 중 하나를 선택해주세요."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<MemberSignUpResponse>> signUp(@RequestBody @Valid final MemberSignUpRequest request);

    @Operation(
            summary = "로그인"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "이메일 또는 비밀번호가 null인 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "email": "이메일은 필수 입력값입니다.",
                                                          "password": "비밀번호는 필수 입력값입니다."
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
                                            name = "회원 미존재",
                                            description = "입력한 이메일로 가입된 회원이 없는 경우",
                                            value = """
                                                      {
                                                        "code": "M-40401",
                                                        "message": "존재하지 않는 회원입니다. 회원가입을 진행해주세요."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "비밀번호 불일치",
                                            description = "입력한 비밀번호가 틀린 경우",
                                            value = """
                                                      {
                                                        "code": "A-40008",
                                                        "message": "비밀번호가 올바르지 않습니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<MemberLoginResponse>> login(@RequestBody @Valid final MemberLoginRequest request);

    @Operation(
            summary = "로그아웃"
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
    ResponseEntity<EdukitResponse<Void>> logout(@MemberId final long memberId);

    @Operation(
            summary = "토큰 갱신"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "리프레시 토큰 누락",
                                            description = "쿠키에 리프레시 토큰이 없는 경우",
                                            value = """
                                                      {
                                                        "code": "A-40101",
                                                        "message": "토큰이 누락되었습니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 리프레시 토큰",
                                            description = "만료되거나 잘못된 리프레시 토큰인 경우",
                                            value = """
                                                      {
                                                        "code": "A-40102",
                                                        "message": "유효하지 않은 토큰입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "토큰 불일치",
                                            description = "서버에 저장된 리프레시 토큰과 다른 경우 (자동 로그아웃 처리)",
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
    ResponseEntity<EdukitResponse<MemberReissueResponse>> reissue(
            @CookieValue(value = "refreshToken") final String refreshToken
    );

    @Operation(
            summary = "교사 인증 이메일 발송"
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
    ResponseEntity<EdukitResponse<Void>> sendVerificationEmail(@MemberId final long memberId);

    @Operation(
            summary = "이메일 인증 처리"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "회원 미존재",
                                            description = "인증하려는 회원이 존재하지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "M-40401",
                                                        "message": "존재하지 않는 회원입니다. 회원가입을 진행해주세요."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "인증 코드 미존재",
                                            description = "유효한 인증 코드가 존재하지 않는 경우",
                                            value = """
                                                      {
                                                        "code": "A-40411",
                                                        "message": "유효한 인증 코드가 존재하지 않습니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "잘못된 인증 코드",
                                            description = "인증 코드가 틀렸거나 만료된 경우",
                                            value = """
                                                      {
                                                        "code": "A-40102",
                                                        "message": "유효하지 않은 토큰입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "인증 코드 시도 횟수 초과",
                                            description = "인증 코드 입력 시도 횟수를 초과한 경우",
                                            value = """
                                                      {
                                                        "code": "M-40013",
                                                        "message": "인증 코드 시도 횟수를 초과했습니다. 인증 코드를 새로 발급받아주세요."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> verifyEmail(
            @RequestParam("id") final String memberUuid,
            @RequestParam("code") final String verificationCode
    );

    @Operation(
            summary = "닉네임 유효성 검사"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "닉네임 파라미터 누락",
                                            description = "nickname 파라미터가 누락된 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "필수 요청 파라미터가 누락되었습니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<MemberNicknameValidationResponse>> validateNickname(
            @RequestParam final String nickname
    );
}
