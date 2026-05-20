package com.dku.emptybear.domain.recommend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendClassroomResponseDto {

    private Long classroomId;

    private String buildingName;

    private String roomName;

    private Integer floor;

    private Boolean hasOutlet;

    private Boolean isFavorite;

    private String availabilityStatus;

    private Integer availableMinutes;

    private String nextClassStartTime;

    // 프론트에서 안 보여줘도 되지만, 개발 중 점수 확인용으로 유용함
    private Double recommendationScore;
}