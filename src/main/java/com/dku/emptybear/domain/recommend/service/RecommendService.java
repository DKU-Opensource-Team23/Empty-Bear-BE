package com.dku.emptybear.domain.recommend.service;

import com.dku.emptybear.domain.classroom.entity.Classroom;
import com.dku.emptybear.domain.classroom.entity.Schedule;
import com.dku.emptybear.domain.classroom.repository.ClassroomRepository;
import com.dku.emptybear.domain.classroom.repository.ScheduleRepository;
import com.dku.emptybear.domain.recommend.dto.request.RecommendRequestDto;
import com.dku.emptybear.domain.recommend.dto.response.RecommendClassroomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendService {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private final ClassroomRepository classroomRepository;
    private final ScheduleRepository scheduleRepository;
    private final AvailabilityService availabilityService;

    public List<RecommendClassroomResponseDto> recommendClassrooms(
            RecommendRequestDto request
    ) {
        RecommendCondition condition = RecommendCondition.from(request);

        LocalDateTime now = LocalDateTime.now();
        String today = convertDayOfWeek(now.getDayOfWeek());

        List<Classroom> classrooms = classroomRepository.findClassroomsByFilters(
                condition.preferredBuildingId(),
                null,
                condition.needOutlet() ? true : null
        );

        List<Long> classroomIds = classrooms.stream()
                .map(Classroom::getClassroomId)
                .toList();

        Map<Long, List<Schedule>> schedulesByClassroomId = classroomIds.isEmpty()
                ? Map.of()
                : scheduleRepository.findByClassroom_ClassroomIdInAndDayOfWeekOrderByStartTimeAsc(
                                classroomIds,
                                today
                        )
                        .stream()
                        .collect(Collectors.groupingBy(schedule -> schedule.getClassroom().getClassroomId()));

        return classrooms.stream()
                .map(classroom -> createCandidate(
                        classroom,
                        schedulesByClassroomId.getOrDefault(classroom.getClassroomId(), List.of()),
                        condition,
                        now
                ))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(RecommendCandidate::score).reversed())
                .map(RecommendCandidate::response)
                .toList();
    }

    private Optional<RecommendCandidate> createCandidate(
            Classroom classroom,
            List<Schedule> schedules,
            RecommendCondition condition,
            LocalDateTime now
    ) {
        AvailabilityService.AvailabilityResult availability =
                availabilityService.calculateAvailability(schedules, now);

        if (!availability.available()) {
            return Optional.empty();
        }

        if (availability.availableMinutes() < condition.minAvailableTime()) {
            return Optional.empty();
        }

        double score = calculateScore(
                classroom.getRoomName(),
                availability.availableMinutes(),
                condition
        );

        RecommendClassroomResponseDto response =
                RecommendClassroomResponseDto.builder()
                        .classroomId(classroom.getClassroomId())
                        .buildingName(classroom.getBuilding().getBuildingName())
                        .roomName(classroom.getRoomName())
                        .floor(classroom.getFloor())
                        .hasOutlet(classroom.getHasOutlet())
                        .isFavorite(false)
                        .availabilityStatus(availability.availabilityStatus())
                        .availableMinutes(availability.availableMinutes())
                        .nextClassStartTime(formatTime(availability))
                        .recommendationScore(score)
                        .build();

        return Optional.of(new RecommendCandidate(response, score));
    }

    private double calculateScore(
            String roomName,
            int availableMinutes,
            RecommendCondition condition
    ) {
        double timeScore = calculateTimeScore(
                availableMinutes,
                condition.minAvailableTime()
        );

        double roomScore = calculateRoomScore(roomName);

        return timeScore * 0.8
                + roomScore * 0.2;
    }

    private double calculateTimeScore(
            int availableMinutes,
            int minAvailableTime
    ) {
        int targetTime = Math.max(minAvailableTime, 60);

        double ratio = (double) availableMinutes / targetTime;

        // 목표 시간의 2배 이상 비어 있으면 시간 점수 만점
        return Math.min(ratio, 2.0) / 2.0;
    }

    private double calculateRoomScore(String roomName) {
        return roomName == null || roomName.isBlank() ? 0.0 : 1.0;
    }

    private String formatTime(
            AvailabilityService.AvailabilityResult availability
    ) {
        if (availability.nextClassStartTime() == null) {
            return null;
        }

        return availability.nextClassStartTime().format(TIME_FORMATTER);
    }

    private String convertDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "MON";
            case TUESDAY -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY -> "THU";
            case FRIDAY -> "FRI";
            case SATURDAY -> "SAT";
            case SUNDAY -> "SUN";
        };
    }

    private record RecommendCandidate(
            RecommendClassroomResponseDto response,
            double score
    ) {
    }

    private record RecommendCondition(
            Long preferredBuildingId,
            int minAvailableTime,
            boolean needOutlet
    ) {
        static RecommendCondition from(RecommendRequestDto request) {
            int minAvailableTime =
                    request.getMinAvailableTime() == null
                            ? 0
                            : request.getMinAvailableTime();

            boolean needOutlet =
                    Boolean.TRUE.equals(request.getNeedOutlet());

            return new RecommendCondition(
                    request.getPreferredBuildingId(),
                    minAvailableTime,
                    needOutlet
            );
        }
    }
}
