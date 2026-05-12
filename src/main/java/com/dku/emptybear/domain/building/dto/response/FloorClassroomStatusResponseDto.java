package com.dku.emptybear.domain.building.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class FloorClassroomStatusResponseDto {

    @Schema(description = "건물 고유 ID", example = "1")
    private Long buildingId;

    @Schema(description = "건물명", example = "소프트웨어ICT관")
    private String buildingName;

    @Schema(description = "층의 내부 값", example = "5")
    private Integer floorValue;

    @Schema(description = "사용자에게 표시할 층 이름", example = "5F")
    private String floorLabel;

    @Schema(description = "강의실 상태 목록")
    private List<ClassroomStatusDto> classrooms;

    @Getter
    @Builder
    public static class ClassroomStatusDto {

        @Schema(description = "강의실 고유 ID", example = "12")
        private Long classroomId;

        @Schema(description = "강의실 호수", example = "516")
        private String roomName;

        @Schema(description = "평면도 이미지 기준 가로 비율 좌표", example = "0.4210")
        private BigDecimal mapX;

        @Schema(description = "평면도 이미지 기준 세로 비율 좌표", example = "0.3375")
        private BigDecimal mapY;

        @Schema(description = "현재 사용 상태 구분값", example = "AVAILABLE_LONG")
        private String availabilityStatus;

        @Schema(description = "현재 시점 기준 남은 사용 가능 시간(분)", example = "45")
        private Integer availableMinutes;

        @Schema(description = "다음 강의 시작 시각", example = "15:30", nullable = true)
        private LocalTime nextClassStartTime;
    }
}