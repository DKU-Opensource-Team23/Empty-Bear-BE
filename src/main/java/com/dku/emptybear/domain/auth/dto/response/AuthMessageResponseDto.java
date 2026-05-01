package com.dku.emptybear.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthMessageResponseDto {

    @Schema(description = "처리 결과 메시지", example = "로그아웃에 성공했습니다.")
    private String message;
}