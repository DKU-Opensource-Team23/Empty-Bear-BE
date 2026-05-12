package com.dku.emptybear.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class UpdateUserPreferenceRequestDto {

    @Schema(description = "선호 건물 ID", example = "3")
    @Positive(message = "선호 건물 ID는 양수여야 합니다.")
    private Long preferredBuildingId;

    @Schema(description = "최소 사용 가능 시간(분)", example = "60")
    @Min(value = 1, message = "최소 사용 가능 시간은 1분 이상이어야 합니다.")
    private Integer minAvailableTime;

    @Schema(description = "콘센트 필요 여부", example = "true")
    private Boolean needOutlet;
}