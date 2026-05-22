
package com.dku.emptybear.domain.recommend.service;

import com.dku.emptybear.domain.classroom.entity.Schedule;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
public class AvailabilityService {

    private static final LocalTime DEFAULT_DAY_END_TIME = LocalTime.of(22, 0);

    public AvailabilityResult calculateAvailability(
            List<Schedule> schedules,
            LocalDateTime now
    ) {
        LocalTime currentTime = now.toLocalTime();

        if (!currentTime.isBefore(DEFAULT_DAY_END_TIME)) {
            return new AvailabilityResult(
                    false,
                    0,
                    null,
                    "UNAVAILABLE"
            );
        }

        List<Schedule> sortedSchedules = schedules.stream()
                .filter(schedule -> schedule.getStartTime() != null)
                .filter(schedule -> schedule.getEndTime() != null)
                .filter(schedule -> schedule.getStartTime().isBefore(schedule.getEndTime()))
                .sorted(Comparator.comparing(Schedule::getStartTime))
                .toList();

        for (Schedule schedule : sortedSchedules) {
            LocalTime startTime = schedule.getStartTime();
            LocalTime endTime = schedule.getEndTime();

            // 현재 시간이 다음 수업 시작 전이면, 지금부터 그 수업 전까지 사용 가능
            if (currentTime.isBefore(startTime)) {
                int availableMinutes = calculateMinutes(currentTime, startTime);

                return new AvailabilityResult(
                        availableMinutes > 0,
                        availableMinutes,
                        startTime,
                        getAvailabilityStatus(availableMinutes)
                );
            }

            // 현재 시간이 수업 시간 안에 있으면 사용 불가
            if (!currentTime.isBefore(startTime) && currentTime.isBefore(endTime)) {
                return new AvailabilityResult(
                        false,
                        0,
                        null,
                        "OCCUPIED"
                );
            }
        }

        // 오늘 남은 수업이 없으면 기본 종료 시각까지 사용 가능
        int availableMinutes = calculateMinutes(currentTime, DEFAULT_DAY_END_TIME);

        return new AvailabilityResult(
                availableMinutes > 0,
                Math.max(availableMinutes, 0),
                null,
                getAvailabilityStatus(availableMinutes)
        );
    }

    private int calculateMinutes(LocalTime from, LocalTime to) {
        return (int) Duration.between(from, to).toMinutes();
    }

    private String getAvailabilityStatus(int availableMinutes) {
        if (availableMinutes <= 0) {
            return "UNAVAILABLE";
        }

        if (availableMinutes >= 120) {
            return "AVAILABLE_LONG";
        }

        return "AVAILABLE_SHORT";
    }

    public record AvailabilityResult(
            boolean available,
            int availableMinutes,
            LocalTime nextClassStartTime,
            String availabilityStatus
    ) {
    }
}
