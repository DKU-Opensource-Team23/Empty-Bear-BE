package com.dku.emptybear.domain.recommend.service;

import com.dku.emptybear.domain.classroom.entity.Classroom;
import com.dku.emptybear.domain.classroom.entity.Schedule;
import com.dku.emptybear.domain.classroom.repository.ClassroomRepository;
import com.dku.emptybear.domain.classroom.repository.ScheduleRepository;
import com.dku.emptybear.domain.favorite.entity.Favorite;
import com.dku.emptybear.domain.favorite.repository.FavoriteRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendService {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private final ClassroomRepository classroomRepository;
    private final ScheduleRepository scheduleRepository;
    private final FavoriteRepository favoriteRepository;
    private final AvailabilityService availabilityService;

    public RecommendClassroomResponseDto recommendClassrooms(
            Long userId,
            RecommendRequestDto request
    ) {
        RecommendCondition condition = RecommendCondition.from(request);

        LocalDateTime now = LocalDateTime.now();
        String today = convertDayOfWeek(now.getDayOfWeek());

        List<Classroom> classrooms = classroomRepository.findClassroomsByFilters(
                condition.buildingId(),
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

        Set<Long> favoriteClassroomIds = getFavoriteClassroomIds(userId, classroomIds);

        List<RecommendClassroomResponseDto.ClassroomDto> recommendedClassrooms = classrooms.stream()
                .map(classroom -> createCandidate(
                        classroom,
                        schedulesByClassroomId.getOrDefault(classroom.getClassroomId(), List.of()),
                        favoriteClassroomIds.contains(classroom.getClassroomId()),
                        condition,
                        now
                ))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(RecommendCandidate::score).reversed())
                .map(RecommendCandidate::response)
                .toList();

        return RecommendClassroomResponseDto.builder()
                .classrooms(recommendedClassrooms)
                .build();
    }

    private Optional<RecommendCandidate> createCandidate(
            Classroom classroom,
            List<Schedule> schedules,
            boolean favorite,
            RecommendCondition condition,
            LocalDateTime now
    ) {
        AvailabilityService.AvailabilityResult availability =
                availabilityService.calculateAvailability(schedules, now);

        if (!availability.available()) {
            return Optional.empty();
        }

        if (availability.availableMinutes() < condition.minAvailableMinutes()) {
            return Optional.empty();
        }

        double score = calculateScore(
                classroom.getRoomName(),
                classroom.getHasOutlet(),
                favorite,
                availability.availableMinutes(),
                condition
        );

        RecommendClassroomResponseDto.ClassroomDto response =
                RecommendClassroomResponseDto.ClassroomDto.builder()
                        .classroomId(classroom.getClassroomId())
                        .buildingName(classroom.getBuilding().getBuildingName())
                        .classroomName(classroom.getRoomName())
                        .availableHour(toAvailableHour(availability.availableMinutes()))
                        .availableMinute(toAvailableMinute(availability.availableMinutes()))
                        .nextClassTime(formatTime(availability))
                        .hasOutlet(classroom.getHasOutlet())
                        .isFavorite(favorite)
                        .availabilityStatus(availability.availabilityStatus())
                        .build();

        return Optional.of(new RecommendCandidate(response, score));
    }

    private double calculateScore(
            String roomName,
            Boolean hasOutlet,
            boolean favorite,
            int availableMinutes,
            RecommendCondition condition
    ) {
        double timeScore = calculateTimeScore(
                availableMinutes,
                condition.minAvailableMinutes()
        );

        double roomScore = calculateRoomScore(roomName);
        double outletScore = calculateOutletScore(hasOutlet, condition.needOutlet());
        double favoriteScore = favorite ? 1.0 : 0.0;

        return timeScore * 0.65
                + roomScore * 0.15
                + outletScore * 0.1
                + favoriteScore * 0.1;
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

    private double calculateOutletScore(Boolean hasOutlet, boolean needOutlet) {
        if (!needOutlet) {
            return 0.5;
        }

        return Boolean.TRUE.equals(hasOutlet) ? 1.0 : 0.0;
    }

    private Set<Long> getFavoriteClassroomIds(Long userId, List<Long> classroomIds) {
        if (userId == null || classroomIds.isEmpty()) {
            return Set.of();
        }

        return favoriteRepository.findByUser_UserIdAndClassroom_ClassroomIdIn(userId, classroomIds)
                .stream()
                .map(Favorite::getClassroom)
                .map(Classroom::getClassroomId)
                .collect(Collectors.toSet());
    }

    private String formatTime(
            AvailabilityService.AvailabilityResult availability
    ) {
        if (availability.nextClassStartTime() == null) {
            return null;
        }

        return availability.nextClassStartTime().format(TIME_FORMATTER);
    }

    private int toAvailableHour(int availableMinutes) {
        return availableMinutes / 60;
    }

    private int toAvailableMinute(int availableMinutes) {
        return availableMinutes % 60;
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
            RecommendClassroomResponseDto.ClassroomDto response,
            double score
    ) {
    }

    private record RecommendCondition(
            Long buildingId,
            int minAvailableMinutes,
            boolean needOutlet
    ) {
        static RecommendCondition from(RecommendRequestDto request) {
            int minAvailableMinutes = calculateMinAvailableMinutes(request);

            boolean needOutlet =
                    Boolean.TRUE.equals(request.getNeedOutlet());

            return new RecommendCondition(
                    request.getBuildingId(),
                    minAvailableMinutes,
                    needOutlet
            );
        }

        private static int calculateMinAvailableMinutes(RecommendRequestDto request) {
            int hour = request.getMinAvailableHour() == null
                    ? 0
                    : request.getMinAvailableHour();

            int minute = request.getMinAvailableMinute() == null
                    ? 0
                    : request.getMinAvailableMinute();

            return Math.addExact(Math.multiplyExact(hour, 60), minute);
        }
    }
}
