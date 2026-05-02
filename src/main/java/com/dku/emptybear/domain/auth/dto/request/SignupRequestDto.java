package com.dku.emptybear.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @Schema(description = "사용자 로그인 아이디", example = "emptybear01")
    @NotBlank(message = "로그인 아이디는 필수입니다.")
    private String loginId;

    @Schema(description = "사용자 비밀번호", example = "qwer1234!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "사용자 닉네임", example = "비었곰")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @Schema(description = "사용자 학번", example = "32231234")
    @NotBlank(message = "학번은 필수입니다.")
    @Pattern(regexp = "^\\d{8}$", message = "학번은 8자리 숫자여야 합니다.")
    private String studentNumber;

    @Schema(description = "사용자 학과", example = "소프트웨어학과")
    @NotBlank(message = "학과는 필수입니다.")
    private String department;
}