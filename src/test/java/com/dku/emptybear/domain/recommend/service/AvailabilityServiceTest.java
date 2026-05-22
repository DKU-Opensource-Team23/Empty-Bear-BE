package com.dku.emptybear.domain.recommend.service;

import com.dku.emptybear.domain.classroom.entity.Schedule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityServiceTest {

    private final AvailabilityService availabilityService = new AvailabilityService();

    @Test
    void calculateAvailabilityReturnsOccupiedWhenCurrentTimeIsInsideSchedule() {
        Schedule schedule = Schedule.builder()
                .dayOfWeek("MON")
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .subjectName("자료구조")
                .professorName("설진석")
                .build();

        AvailabilityService.AvailabilityResult result =
                availabilityService.calculateAvailability(
                        List.of(schedule),
                        LocalDateTime.of(2026, 5, 18, 10, 30)
                );

        assertThat(result.available()).isFalse();
        assertThat(result.availableMinutes()).isZero();
        assertThat(result.availabilityStatus()).isEqualTo("OCCUPIED");
    }

    @Test
    void calculateAvailabilityReturnsMinutesUntilNextClass() {
        Schedule schedule = Schedule.builder()
                .dayOfWeek("MON")
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(14, 30))
                .subjectName("데이터베이스")
                .professorName("오세종")
                .build();

        AvailabilityService.AvailabilityResult result =
                availabilityService.calculateAvailability(
                        List.of(schedule),
                        LocalDateTime.of(2026, 5, 18, 12, 15)
                );

        assertThat(result.available()).isTrue();
        assertThat(result.availableMinutes()).isEqualTo(45);
        assertThat(result.nextClassStartTime()).isEqualTo(LocalTime.of(13, 0));
        assertThat(result.availabilityStatus()).isEqualTo("AVAILABLE_SHORT");
    }
}
