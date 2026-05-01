package com.dku.emptybear.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponseDto {

    private UserInfoDto user;
    private String message;

    @Getter
    @Builder
    public static class UserInfoDto {
        private Long userId;
        private String loginId;
    }
}