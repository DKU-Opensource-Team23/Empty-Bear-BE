package com.dku.emptybear.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateMyInfoRequestDto {

    @Schema(description = "수정할 사용자 닉네임", example = "비었곰")
    private String nickname;

    @Schema(description = "수정할 사용자 학번", example = "32231234")
    @Pattern(regexp = "^\\d{8}$", message = "학번은 8자리 숫자여야 합니다.")
    private String studentNumber;

    @Schema(description = "수정할 사용자 학과", example = "소프트웨어학과")
    private String department;
}