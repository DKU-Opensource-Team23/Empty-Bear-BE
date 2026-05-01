package com.dku.emptybear.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {

    @Schema(description = "로그인 아이디", example = "emptybear01")
    @NotBlank(message = "로그인 아이디는 필수입니다.")
    private String loginId;

    @Schema(description = "로그인 비밀번호", example = "qwer1234!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}