package com.emptybear.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @NotBlank(message = "로그인 아이디는 필수입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "학번은 필수입니다.")
    private String studentNumber;

    @NotBlank(message = "학과는 필수입니다.")
    private String department;
}