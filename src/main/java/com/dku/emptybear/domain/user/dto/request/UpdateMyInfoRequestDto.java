package com.dku.emptybear.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateMyInfoRequestDto {

    @Schema(description = "수정할 사용자 닉네임", example = "비었곰")
    private String nickname;

    @Schema(description = "수정할 사용자 학번", example = "32231234")
    private String studentNumber;

    @Schema(description = "수정할 사용자 학과", example = "소프트웨어학과")
    private String department;
}