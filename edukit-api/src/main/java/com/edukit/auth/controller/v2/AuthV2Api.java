package com.edukit.auth.controller.v2;

import com.edukit.auth.controller.request.PasswordFindRequest;
import com.edukit.auth.controller.request.UpdatePasswordRequest;
import com.edukit.common.EdukitResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "인증/인가 Ver2", description = "회원가입 전 비밀번호 변경 API 등")
public interface AuthV2Api {

    @Operation(
            summary = "비밀번호 찾기",
            description = "이메일로 비밀번호 재설정 페이지 링크를 발송합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "비밀번호 찾기 이메일 발송 성공",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "message": "요청이 성공했습니다."
                                            }
                                            """
                            )
                    )
            ),
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
                                                          "email": "이메일 주소를 입력해주세요."
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
                                                          "email": "유효한 이메일 형식이 아닙니다."
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
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> findPassword(@RequestBody @Valid final PasswordFindRequest request);

    @Operation(
            summary = "비밀번호 변경",
            description = """
                    비밀번호 재설정 페이지에서 비밀번호를 변경합니다.
                    재설정 페이지 링크: https://{도메인 주소}/reset-password?id=%s&code=%s
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "비밀번호 변경 성공",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "message": "요청이 성공했습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "ERROR",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            description = "memberUuid, verificationCode, password 중 하나라도 null인 경우",
                                            value = """
                                                      {
                                                        "code": "FAIL-400",
                                                        "message": "validation 오류",
                                                        "data": {
                                                          "memberUuid": "유저 uuid를 입력해주세요.",
                                                          "verificationCode": "인증 코드는 필수입니다.",
                                                          "password": "새 비밀번호를 입력해주세요."
                                                        }
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "비밀번호 형식 오류",
                                            description = "비밀번호가 규칙에 맞지 않는 경우 (8-16자, 영문/숫자/특수문자 중 2가지 이상, 동일 문자 3번 연속 금지)",
                                            value = """
                                                      {
                                                        "code": "A-40007",
                                                        "message": "유효하지 않은 비밀번호 양식입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "기존과 동일한 비밀번호",
                                            description = "새로운 비밀번호가 기존 비밀번호와 같은 경우",
                                            value = """
                                                      {
                                                        "code": "A-40009",
                                                        "message": "새로운 비밀번호는 기존 비밀번호와 같을 수 없습니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "회원 미존재",
                                            description = "memberUuid에 해당하는 회원이 존재하지 않는 경우",
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
                                                        "code": "A-40013",
                                                        "message": "인증 코드 시도 횟수를 초과했습니다. 인증 코드를 새로 발급받아주세요."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<EdukitResponse<Void>> updatePassword(@RequestBody @Valid final UpdatePasswordRequest request);
}
