package com.dku.emptybear.domain.classroom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ClassroomDetailResponseDto {

    @Schema(description = "강의실 상세 정보")
    private ClassroomDetailDto classroom;

    @Getter
    @Builder
    public static class ClassroomDetailDto {

        @Schema(description = "강의실 고유 ID", example = "12")
        private Long classroomId;

        @Schema(description = "건물 정보")
        private BuildingInfoDto building;

        @Schema(description = "강의실 호수", example = "516")
        private String roomName;

        @Schema(description = "층의 내부 값", example = "5")
        private Integer floorValue;

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

        @Schema(description = "리뷰 요약")
        private ReviewSummaryDto reviewSummary;
    }

    @Getter
    @Builder
    public static class BuildingInfoDto {

        @Schema(description = "건물 고유 ID", example = "1")
        private Long buildingId;

        @Schema(description = "건물명", example = "소프트웨어ICT관")
        private String buildingName;
    }

    @Getter
    @Builder
    public static class ReviewSummaryDto {

        @Schema(description = "전체 리뷰 개수", example = "18")
        private Integer totalReviewCount;

        @Schema(description = "태그별 리뷰 개수")
        private List<TagSummaryDto> tags;
    }

    @Getter
    @Builder
    public static class TagSummaryDto {

        @Schema(description = "태그 고유 ID", example = "1")
        private Long tagId;

        @Schema(description = "태그 내부 코드값", example = "QUIET")
        private String code;

        @Schema(description = "태그 표시명", example = "조용해요")
        private String displayName;

        @Schema(description = "해당 태그가 선택된 리뷰 개수", example = "8")
        private Integer count;
    }
}