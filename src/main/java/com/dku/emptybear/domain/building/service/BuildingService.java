package com.dku.emptybear.domain.building.service;

import com.dku.emptybear.domain.building.dto.response.BuildingListResponseDto;
import com.dku.emptybear.domain.building.dto.response.FloorClassroomStatusResponseDto;
import com.dku.emptybear.domain.building.dto.response.FloorListResponseDto;
import com.dku.emptybear.domain.building.dto.response.FloorPlanResponseDto;
import com.dku.emptybear.domain.building.entity.Building;
import com.dku.emptybear.domain.building.entity.FloorPlan;
import com.dku.emptybear.domain.building.repository.BuildingRepository;
import com.dku.emptybear.domain.building.repository.FloorPlanRepository;
import com.dku.emptybear.domain.classroom.entity.Classroom;
import com.dku.emptybear.domain.classroom.entity.Schedule;
import com.dku.emptybear.domain.classroom.repository.ClassroomRepository;
import com.dku.emptybear.domain.classroom.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingService {

    private static final int AVAILABLE_LONG_THRESHOLD_MINUTES = 30;

    private final BuildingRepository buildingRepository;
    private final FloorPlanRepository floorPlanRepository;
    private final ClassroomRepository classroomRepository;
    private final ScheduleRepository scheduleRepository;

    public BuildingListResponseDto getBuildings() {
        List<Building> buildings = buildingRepository.findAll();

        return BuildingListResponseDto.from(buildings);
    }

    public FloorListResponseDto getFloors(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 건물입니다."));

        List<FloorPlan> floorPlans = floorPlanRepository.findByBuilding_BuildingIdOrderByFloorAsc(buildingId);

        return FloorListResponseDto.of(building, floorPlans);
    }

    public FloorPlanResponseDto getFloorPlan(Long buildingId, Integer floorValue) {
        FloorPlan floorPlan = floorPlanRepository.findByBuilding_BuildingIdAndFloor(buildingId, floorValue)
                .orElseThrow(() -> new IllegalArgumentException("해당 층의 평면도가 존재하지 않습니다."));

        return FloorPlanResponseDto.from(floorPlan);
    }

    public FloorClassroomStatusResponseDto getClassroomStatuses(Long buildingId, Integer floorValue) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 건물입니다."));

        List<Classroom> classrooms = classroomRepository
                .findByBuilding_BuildingIdAndFloorOrderByRoomNameAsc(buildingId, floorValue);

        List<Long> classroomIds = classrooms.stream()
                .map(Classroom::getClassroomId)
                .toList();

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

        List<FloorClassroomStatusResponseDto.ClassroomStatusDto> classroomStatusDtos = classrooms.stream()
                .map(classroom -> {
                    List<Schedule> classroomSchedules = scheduleMap.getOrDefault(
                            classroom.getClassroomId(),
                            List.of()
                    );

                    ClassroomAvailability availability = calculateAvailability(classroomSchedules, now);

                    return FloorClassroomStatusResponseDto.ClassroomStatusDto.builder()
                            .classroomId(classroom.getClassroomId())
                            .roomName(classroom.getRoomName())
                            .mapX(classroom.getMapX())
                            .mapY(classroom.getMapY())
                            .availabilityStatus(availability.status())
                            .availableMinutes(availability.availableMinutes())
                            .nextClassStartTime(availability.nextClassStartTime())
                            .build();
                })
                .toList();

        return FloorClassroomStatusResponseDto.builder()
                .buildingId(building.getBuildingId())
                .buildingName(building.getBuildingName())
                .floorValue(floorValue)
                .floorLabel(toFloorLabel(floorValue))
                .classrooms(classroomStatusDtos)
                .build();
    }

    private ClassroomAvailability calculateAvailability(
            List<Schedule> schedules,
            LocalTime now
    ) {
        List<Schedule> sortedSchedules = schedules.stream()
                .sorted(Comparator.comparing(Schedule::getStartTime))
                .toList();

        for (Schedule schedule : sortedSchedules) {
            if (!now.isBefore(schedule.getStartTime()) && now.isBefore(schedule.getEndTime())) {
                return new ClassroomAvailability(
                        "IN_USE",
                        0,
                        null
                );
            }

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
                0,
                null
        );
    }

    private String resolveAvailableStatus(int availableMinutes) {
        if (availableMinutes >= AVAILABLE_LONG_THRESHOLD_MINUTES) {
            return "AVAILABLE_LONG";
        }

        return "AVAILABLE_SHORT";
    }

    private int calculateMinutesBetween(LocalTime start, LocalTime end) {
        return (int) java.time.Duration.between(start, end).toMinutes();
    }

    private String getTodayValue() {
        DayOfWeek dayOfWeek = java.time.LocalDate.now().getDayOfWeek();

        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
    }

    private String toFloorLabel(Integer floor) {
        if (floor == null) {
            return null;
        }

        if (floor < 0) {
            return "B" + Math.abs(floor);
        }

        return floor + "F";
    }

    private record ClassroomAvailability(
            String status,
            Integer availableMinutes,
            LocalTime nextClassStartTime
    ) {
    }
}