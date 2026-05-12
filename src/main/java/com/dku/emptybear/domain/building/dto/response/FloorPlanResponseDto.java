package com.dku.emptybear.domain.building.dto.response;

import com.dku.emptybear.domain.building.entity.FloorPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FloorPlanResponseDto {

    @Schema(description = "평면도 정보")
    private FloorPlanInfoDto floorPlan;

    @Getter
    @Builder
    public static class FloorPlanInfoDto {

        @Schema(description = "평면도 고유 ID", example = "3")
        private Long floorPlanId;

        @Schema(description = "건물 고유 ID", example = "1")
        private Long buildingId;

        @Schema(description = "건물명", example = "소프트웨어ICT관")
        private String buildingName;

        @Schema(description = "층의 내부 값", example = "-1")
        private Integer floorValue;

        @Schema(description = "사용자에게 표시할 층 이름", example = "B1")
        private String floorLabel;

        @Schema(description = "평면도 이미지 URL", example = "https://example.com/floorplans/ict_b1.png")
        private String imageUrl;
    }

    public static FloorPlanResponseDto from(FloorPlan floorPlan) {
        return FloorPlanResponseDto.builder()
                .floorPlan(FloorPlanInfoDto.builder()
                        .floorPlanId(floorPlan.getFloorPlanId())
                        .buildingId(floorPlan.getBuilding().getBuildingId())
                        .buildingName(floorPlan.getBuilding().getBuildingName())
                        .floorValue(floorPlan.getFloor())
                        .floorLabel(toFloorLabel(floorPlan.getFloor()))
                        .imageUrl(floorPlan.getImageUrl())
                        .build())
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