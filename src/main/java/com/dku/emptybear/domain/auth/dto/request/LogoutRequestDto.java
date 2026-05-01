package com.dku.emptybear.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutRequestDto {

    @Schema(description = "로그아웃 처리 시 무효화할 리프레시 토큰", example = "refresh-token-example")
    @NotBlank(message = "리프레시 토큰이 없습니다.")
    private String refreshToken;
}