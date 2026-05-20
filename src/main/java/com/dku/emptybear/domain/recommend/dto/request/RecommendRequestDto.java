package com.dku.emptybear.domain.recommend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendRequestDto {

    // 사용자가 선호하는 건물 ID
    private Long preferredBuildingId;

    // 사용자가 강의실을 사용하고 싶은 최소 시간, 단위: 분
    private Integer minAvailableTime;

    // 콘센트 필요 여부
    private Boolean needOutlet;
}
