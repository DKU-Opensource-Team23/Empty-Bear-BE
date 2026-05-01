package com.dku.emptybear.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReissueRequestDto {

    @Schema(description = "액세스 토큰 재발급에 사용하는 리프레시 토큰", example = "refresh-token-example")
    @NotBlank(message = "리프레시 토큰이 없습니다.")
    private String refreshToken;
}