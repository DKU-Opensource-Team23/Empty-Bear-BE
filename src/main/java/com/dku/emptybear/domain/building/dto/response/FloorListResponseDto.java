package com.dku.emptybear.domain.building.dto.response;

import com.dku.emptybear.domain.building.entity.Building;
import com.dku.emptybear.domain.building.entity.FloorPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FloorListResponseDto {

    @Schema(description = "건물 고유 ID", example = "1")
    private Long buildingId;

    @Schema(description = "건물명", example = "소프트웨어ICT관")
    private String buildingName;

    @Schema(description = "층 목록")
    private List<FloorInfoDto> floors;

    @Getter
    @Builder
    public static class FloorInfoDto {

        @Schema(description = "층의 내부 값", example = "-1")
        private Integer floorValue;

        @Schema(description = "사용자에게 표시할 층 이름", example = "B1")
        private String floorLabel;
    }

    public static FloorListResponseDto of(Building building, List<FloorPlan> floorPlans) {
        return FloorListResponseDto.builder()
                .buildingId(building.getBuildingId())
                .buildingName(building.getBuildingName())
                .floors(
                        floorPlans.stream()
                                .map(floorPlan -> FloorInfoDto.builder()
                                        .floorValue(floorPlan.getFloor())
                                        .floorLabel(toFloorLabel(floorPlan.getFloor()))
                                        .build())
                                .toList()
                )
                .build();
    }

    private static String toFloorLabel(Integer floor) {
        if (floor == null) {
            return null;
        }

        if (floor < 0) {
            return "B" + Math.abs(floor);
        }

        return floor + "F";
    }
}