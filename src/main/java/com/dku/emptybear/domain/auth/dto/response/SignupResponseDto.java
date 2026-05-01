package com.dku.emptybear.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponseDto {

    @Schema(description = "생성된 사용자 정보")
    private UserInfoDto user;

    @Schema(description = "회원가입 성공 메시지", example = "회원가입에 성공했습니다.")
    private String message;

    @Getter
    @Builder
    public static class UserInfoDto {

        @Schema(description = "생성된 사용자 고유 ID", example = "1")
        private Long userId;

        @Schema(description = "생성된 사용자 로그인 아이디", example = "emptybear01")
        private String loginId;
    }
}