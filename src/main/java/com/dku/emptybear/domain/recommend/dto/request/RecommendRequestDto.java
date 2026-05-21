package com.dku.emptybear.domain.recommend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendRequestDto {

    @Schema(description = "건물 ID", example = "1", nullable = true)
    private Long buildingId;

    @Schema(description = "최소 사용 가능 시간 중 시간 단위", example = "1", nullable = true)
    @Min(value = 0, message = "최소 사용 가능 시간은 0시간 이상이어야 합니다.")
    private Integer minAvailableHour;

    @Schema(description = "최소 사용 가능 시간 중 분 단위", example = "30", nullable = true)
    @Min(value = 0, message = "최소 사용 가능 시간은 0분 이상이어야 합니다.")
    private Integer minAvailableMinute;

    @Schema(description = "콘센트 필요 여부", example = "true", nullable = true)
    private Boolean needOutlet;
}
