package com.dku.emptybear.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPreferenceResponseDto {

    @Schema(description = "사용자 기본 선호 설정")
    private PreferenceDto preference;

    @Getter
    @Builder
    public static class PreferenceDto {

        @Schema(description = "선호 건물 정보")
        private PreferredBuildingDto preferredBuilding;

        @Schema(description = "최소 사용 가능 시간(분)", example = "60")
        private Integer minAvailableTime;

        @Schema(description = "콘센트 필요 여부", example = "true")
        private Boolean needOutlet;
    }

    @Getter
    @Builder
    public static class PreferredBuildingDto {

        @Schema(description = "선호 건물 ID", example = "3")
        private Long buildingId;

        @Schema(description = "선호 건물명", example = "소프트웨어ICT관")
        private String buildingName;
    }
}