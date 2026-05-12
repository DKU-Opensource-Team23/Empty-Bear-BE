package com.dku.emptybear.domain.building.dto.response;

import com.dku.emptybear.domain.building.entity.Building;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class BuildingListResponseDto {

    @Schema(description = "건물 목록")
    private List<BuildingInfoDto> buildings;

    @Getter
    @Builder
    public static class BuildingInfoDto {

        @Schema(description = "건물 고유 ID", example = "1")
        private Long buildingId;

        @Schema(description = "건물명", example = "소프트웨어ICT관")
        private String buildingName;

        @Schema(description = "캠퍼스명", example = "죽전", nullable = true)
        private String campus;

        @Schema(description = "건물 위도", example = "37.3214567", nullable = true)
        private BigDecimal latitude;

        @Schema(description = "건물 경도", example = "127.1265432", nullable = true)
        private BigDecimal longitude;
    }

    public static BuildingListResponseDto from(List<Building> buildings) {
        return BuildingListResponseDto.builder()
                .buildings(
                        buildings.stream()
                                .map(building -> BuildingInfoDto.builder()
                                        .buildingId(building.getBuildingId())
                                        .buildingName(building.getBuildingName())
                                        .campus(building.getCampus())
                                        .latitude(building.getLatitude())
                                        .longitude(building.getLongitude())
                                        .build())
                                .toList()
                )
                .build();
    }
}