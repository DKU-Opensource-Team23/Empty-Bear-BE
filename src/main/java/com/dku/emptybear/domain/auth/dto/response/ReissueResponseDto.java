package com.dku.emptybear.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReissueResponseDto {

    @Schema(description = "새로 발급된 액세스 토큰", example = "new-access-token-example")
    private String accessToken;
}