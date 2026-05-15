package com.dku.emptybear.domain.classroom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ClassroomWeeklyScheduleResponseDto {

    @Schema(description = "강의실 고유 ID", example = "12")
    private Long classroomId;

    @Schema(description = "강의실 주간 시간표")
    private List<ScheduleInfoDto> weeklySchedule;

    @Getter
    @Builder
    public static class ScheduleInfoDto {

        @Schema(description = "시간표 고유 ID", example = "101")
        private Long scheduleId;

        @Schema(description = "요일", example = "MON")
        private String dayOfWeek;

        @Schema(description = "시작 시각", example = "09:00")
        private String startTime;

        @Schema(description = "종료 시각", example = "10:15")
        private String endTime;

        @Schema(description = "수업명", example = "자료구조", nullable = true)
        private String subjectName;
    }
}