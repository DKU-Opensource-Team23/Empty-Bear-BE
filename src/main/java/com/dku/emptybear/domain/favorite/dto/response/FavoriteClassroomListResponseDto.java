package com.dku.emptybear.domain.favorite.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FavoriteClassroomListResponseDto {

    @Schema(description = "즐겨찾기한 강의실 목록")
    private List<FavoriteClassroomDto> classrooms;

    @Getter
    @Builder
    public static class FavoriteClassroomDto {

        @Schema(description = "강의실 고유 ID", example = "12")
        private Long classroomId;

        @Schema(description = "건물명", example = "소프트웨어ICT관")
        private String buildingName;

        @Schema(description = "강의실 호수", example = "516")
        private String roomName;

        @Schema(description = "층수", example = "5")
        private Integer floor;

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
    }
}