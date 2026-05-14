package com.dku.emptybear.domain.classroom.service;

import com.dku.emptybear.domain.classroom.dto.response.ClassroomOverviewListResponseDto;
import com.dku.emptybear.domain.classroom.entity.Classroom;
import com.dku.emptybear.domain.classroom.entity.Favorite;
import com.dku.emptybear.domain.classroom.entity.Schedule;
import com.dku.emptybear.domain.classroom.repository.ClassroomRepository;
import com.dku.emptybear.domain.classroom.repository.FavoriteRepository;
import com.dku.emptybear.domain.classroom.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassroomService {

    private static final int AVAILABLE_LONG_THRESHOLD_MINUTES = 30;
    private static final LocalTime END_OF_DAY = LocalTime.of(23, 59);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ClassroomRepository classroomRepository;
    private final ScheduleRepository scheduleRepository;
    private final FavoriteRepository favoriteRepository;

    /**
     * 조건에 맞는 강의실 개요 목록을 조회한다.
     * availabilityStatus와 minAvailableTime은 현재 시간표 계산 결과이므로 조회 후 필터링한다.
     */
    public ClassroomOverviewListResponseDto getClassroomOverviews(
            Long userId,
            Long buildingId,
            Integer floorValue,
            Boolean hasOutlet,
            String availabilityStatus,
            Integer minAvailableTime
    ) {
        List<Classroom> classrooms = classroomRepository.findClassroomsByFilters(
                buildingId,
                floorValue,
                hasOutlet
        );

        List<Long> classroomIds = classrooms.stream()
                .map(Classroom::getClassroomId)
                .toList();

        Set<Long> favoriteClassroomIds = getFavoriteClassroomIds(userId, classroomIds);

        String today = getTodayValue();
        LocalTime now = LocalTime.now();

        List<Schedule> schedules = classroomIds.isEmpty()
                ? List.of()
                : scheduleRepository.findByClassroom_ClassroomIdInAndDayOfWeekOrderByStartTimeAsc(
                        classroomIds,
                        today
                );

        Map<Long, List<Schedule>> scheduleMap = schedules.stream()
                .collect(Collectors.groupingBy(schedule -> schedule.getClassroom().getClassroomId()));

        List<ClassroomOverviewListResponseDto.ClassroomOverviewDto> result = classrooms.stream()
                .map(classroom -> {
                    List<Schedule> classroomSchedules = scheduleMap.getOrDefault(
                            classroom.getClassroomId(),
                            List.of()
                    );

                    ClassroomAvailability availability = calculateAvailability(classroomSchedules, now);

                    return ClassroomOverviewListResponseDto.ClassroomOverviewDto.builder()
                            .classroomId(classroom.getClassroomId())
                            .buildingName(classroom.getBuilding().getBuildingName())
                            .roomName(classroom.getRoomName())
                            .floorLabel(toFloorLabel(classroom.getFloor()))
                            .hasOutlet(classroom.getHasOutlet())
                            .isFavorite(favoriteClassroomIds.contains(classroom.getClassroomId()))
                            .availabilityStatus(availability.status())
                            .availableMinutes(availability.availableMinutes())
                            .nextClassStartTime(formatTime(availability.nextClassStartTime()))
                            .build();
                })
                .filter(dto -> matchesAvailabilityStatus(dto, availabilityStatus))
                .filter(dto -> matchesMinAvailableTime(dto, minAvailableTime))
                .toList();

        return ClassroomOverviewListResponseDto.builder()
                .classrooms(result)
                .build();
    }

    /**
     * 로그인 사용자가 즐겨찾기한 강의실 ID 목록을 조회한다.
     */
    private Set<Long> getFavoriteClassroomIds(Long userId, List<Long> classroomIds) {
        if (classroomIds.isEmpty()) {
            return Set.of();
        }

        return favoriteRepository.findByUser_UserIdAndClassroom_ClassroomIdIn(userId, classroomIds)
                .stream()
                .map(Favorite::getClassroom)
                .map(Classroom::getClassroomId)
                .collect(Collectors.toSet());
    }

    /**
     * 요청한 사용 상태 조건과 계산된 강의실 상태가 일치하는지 확인한다.
     */
    private boolean matchesAvailabilityStatus(
            ClassroomOverviewListResponseDto.ClassroomOverviewDto dto,
            String availabilityStatus
    ) {
        if (availabilityStatus == null || availabilityStatus.isBlank()) {
            return true;
        }

        return availabilityStatus.trim().equals(dto.getAvailabilityStatus());
    }

    /**
     * 요청한 최소 사용 가능 시간을 만족하는지 확인한다.
     */
    private boolean matchesMinAvailableTime(
            ClassroomOverviewListResponseDto.ClassroomOverviewDto dto,
            Integer minAvailableTime
    ) {
        if (minAvailableTime == null) {
            return true;
        }

        return dto.getAvailableMinutes() >= minAvailableTime;
    }

    /**
     * 현재 시각과 오늘 시간표를 비교해 강의실 사용 상태를 계산한다.
     */
    private ClassroomAvailability calculateAvailability(
            List<Schedule> schedules,
            LocalTime now
    ) {
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

        return new ClassroomAvailability(
                "AVAILABLE_LONG",
                calculateMinutesBetween(now, END_OF_DAY),
                null
        );
    }

    /**
     * 사용 가능 시간이 기준값 이상이면 AVAILABLE_LONG, 미만이면 AVAILABLE_SHORT로 분류한다.
     */
    private String resolveAvailableStatus(int availableMinutes) {
        if (availableMinutes >= AVAILABLE_LONG_THRESHOLD_MINUTES) {
            return "AVAILABLE_LONG";
        }

        return "AVAILABLE_SHORT";
    }

    /**
     * 두 시각 사이의 차이를 분 단위로 계산한다.
     */
    private int calculateMinutesBetween(LocalTime start, LocalTime end) {
        return (int) Duration.between(start, end).toMinutes();
    }

    /**
     * 오늘 요일을 시간표 테이블의 dayOfWeek 저장 형식에 맞게 변환한다.
     */
    private String getTodayValue() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();

        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
    }

    /**
     * 층 숫자를 사용자 표시용 층 이름으로 변환한다.
     */
    private String toFloorLabel(Integer floor) {
        if (floor == null) {
            return null;
        }

        if (floor < 0) {
            return "B" + Math.abs(floor);
        }

        return floor + "F";
    }

    /**
     * LocalTime을 HH:mm 형식의 문자열로 변환한다.
     */
    private String formatTime(LocalTime time) {
        if (time == null) {
            return null;
        }

        return time.format(TIME_FORMATTER);
    }

    /**
     * 강의실 사용 상태 계산 결과를 담는 내부 값 객체.
     */
    private record ClassroomAvailability(
            String status,
            Integer availableMinutes,
            LocalTime nextClassStartTime
    ) {
    }
}