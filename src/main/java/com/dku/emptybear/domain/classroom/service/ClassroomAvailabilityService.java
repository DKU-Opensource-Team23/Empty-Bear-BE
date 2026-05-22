package com.dku.emptybear.domain.classroom.service;

import com.dku.emptybear.domain.classroom.entity.Schedule;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ClassroomAvailabilityService {

    private static final int AVAILABLE_LONG_THRESHOLD_MINUTES = 30;
    private static final LocalTime END_OF_DAY = LocalTime.of(23, 59);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 현재 시각과 오늘 시간표를 비교해 강의실 사용 상태를 계산한다.
     */
    public ClassroomAvailability calculateAvailability(List<Schedule> schedules, LocalTime now) {
        List<Schedule> sortedSchedules = schedules.stream()
                .sorted(Comparator.comparing(Schedule::getStartTime))
                .toList();

        for (Schedule schedule : sortedSchedules) {
            // 현재 수업 시간에 포함되면 사용 중으로 판단한다.
            if (!now.isBefore(schedule.getStartTime()) && now.isBefore(schedule.getEndTime())) {
                return new ClassroomAvailability(
                        "IN_USE",
                        0,
                        null
                );
            }

            // 다음 수업 시작 전이면 그때까지 사용 가능하다고 판단한다.
            if (now.isBefore(schedule.getStartTime())) {
                int availableMinutes = calculateMinutesBetween(now, schedule.getStartTime());

                return new ClassroomAvailability(
                        resolveAvailableStatus(availableMinutes),
                        availableMinutes,
                        schedule.getStartTime()
                );
            }
        }

        int availableMinutes = calculateMinutesBetween(now, END_OF_DAY);

        return new ClassroomAvailability(
                resolveAvailableStatus(availableMinutes),
                availableMinutes,
                null
        );
    }

    /**
     * 오늘 요일을 시간표 테이블의 dayOfWeek 저장 형식에 맞게 변환한다.
     */
    public String getTodayValue() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();

        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
    }

    /**
     * LocalTime을 HH:mm 형식의 문자열로 변환한다.
     */
    public String formatTime(LocalTime time) {
        if (time == null) {
            return null;
        }

        return time.format(TIME_FORMATTER);
    }

    private String resolveAvailableStatus(int availableMinutes) {
        if (availableMinutes >= AVAILABLE_LONG_THRESHOLD_MINUTES) {
            return "AVAILABLE_LONG";
        }

        return "AVAILABLE_SHORT";
    }

    private int calculateMinutesBetween(LocalTime start, LocalTime end) {
        return (int) Duration.between(start, end).toMinutes();
    }

    @Getter
    public static class ClassroomAvailability {

        private final String status;
        private final Integer availableMinutes;
        private final LocalTime nextClassStartTime;

        public ClassroomAvailability(
                String status,
                Integer availableMinutes,
                LocalTime nextClassStartTime
        ) {
            this.status = status;
            this.availableMinutes = availableMinutes;
            this.nextClassStartTime = nextClassStartTime;
        }
    }
}