package com.dku.emptybear.domain.recommend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendClassroomResponseDto {

    @Schema(description = "추천 강의실 목록")
    private List<ClassroomDto> classrooms;

    @Getter
    @Builder
    public static class ClassroomDto {

        @Schema(description = "강의실 고유 ID", example = "12")
        private Long classroomId;

        @Schema(description = "건물명", example = "소프트웨어ICT관")
        private String buildingName;

        @Schema(description = "강의실명", example = "516")
        private String classroomName;

        @Schema(description = "사용 가능 시간(시)", example = "1")
        private Integer availableHour;

        @Schema(description = "사용 가능 시간(분)", example = "30")
        private Integer availableMinute;

        @Schema(description = "다음 수업 시작 시각", example = "15:00", nullable = true)
        private String nextClassTime;

        @Schema(description = "콘센트 여부", example = "true")
        private Boolean hasOutlet;

        @Schema(description = "로그인 사용자의 즐겨찾기 여부", example = "false")
        private Boolean isFavorite;

        @Schema(description = "현재 사용 상태 구분값", example = "AVAILABLE_LONG")
        private String availabilityStatus;
    }
}
