package com.dku.emptybear.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateMyInfoResponseDto {

    @Schema(description = "수정된 사용자 정보")
    private UserInfoDto user;

    @Getter
    @Builder
    public static class UserInfoDto {

        @Schema(description = "사용자 고유 ID", example = "1")
        private Long userId;

        @Schema(description = "사용자 로그인 아이디", example = "emptybear01")
        private String loginId;

        @Schema(description = "수정된 사용자 닉네임", example = "비었곰")
        private String nickname;

        @Schema(description = "수정된 사용자 학번", example = "32231234")
        private String studentNumber;

        @Schema(description = "수정된 사용자 학과", example = "소프트웨어학과")
        private String department;
    }
}