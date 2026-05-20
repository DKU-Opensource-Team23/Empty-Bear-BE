package com.dku.emptybear.domain.recommend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendClassroomResponseDto {

    @Schema(description = "강의실 고유 ID", example = "12")
    private Long classroomId;

    @Schema(description = "건물명", example = "소프트웨어ICT관")
    private String buildingName;

    @Schema(description = "강의실 호수", example = "516")
    private String roomName;

    @Schema(description = "층 값", example = "5")
    private Integer floor;

    @Schema(description = "사용자에게 표시할 층 이름", example = "5F")
    private String floorLabel;

    @Schema(description = "콘센트 여부", example = "true")
    private Boolean hasOutlet;

    @Schema(description = "로그인 사용자의 즐겨찾기 여부", example = "true")
    private Boolean isFavorite;

    @Schema(description = "현재 사용 상태 구분값", example = "AVAILABLE_LONG")
    private String availabilityStatus;

    @Schema(description = "현재 시점 기준 남은 사용 가능 시간(분)", example = "45")
    private Integer availableMinutes;

    @Schema(description = "다음 강의 시작 시각", example = "15:30", nullable = true)
    private String nextClassStartTime;

    @Schema(description = "추천 점수", example = "0.82")
    private Double recommendationScore;
}
