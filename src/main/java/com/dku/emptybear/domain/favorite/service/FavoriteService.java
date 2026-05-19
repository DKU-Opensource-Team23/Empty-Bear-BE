package com.dku.emptybear.domain.favorite.service;

import com.dku.emptybear.domain.classroom.entity.Classroom;
import com.dku.emptybear.domain.classroom.entity.Schedule;
import com.dku.emptybear.domain.classroom.repository.ScheduleRepository;
import com.dku.emptybear.domain.classroom.service.ClassroomAvailabilityService;
import com.dku.emptybear.domain.classroom.service.ClassroomAvailabilityService.ClassroomAvailability;
import com.dku.emptybear.domain.classroom.repository.ClassroomRepository;

import com.dku.emptybear.domain.favorite.dto.request.AddFavoriteRequestDto;
import com.dku.emptybear.domain.favorite.dto.response.FavoriteStatusResponseDto;
import com.dku.emptybear.domain.favorite.dto.response.FavoriteClassroomListResponseDto;
import com.dku.emptybear.domain.favorite.entity.Favorite;
import com.dku.emptybear.domain.favorite.repository.FavoriteRepository;

import com.dku.emptybear.domain.user.entity.User;
import com.dku.emptybear.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final ClassroomAvailabilityService classroomAvailabilityService;

    /**
     * 로그인 사용자가 즐겨찾기한 강의실 목록을 조회한다.
     */
    public FavoriteClassroomListResponseDto getFavoriteClassrooms(Long userId) {
        List<Favorite> favorites = favoriteRepository.findFavoritesByUserIdWithClassroomAndBuilding(userId);

        List<Long> classroomIds = favorites.stream()
                .map(favorite -> favorite.getClassroom().getClassroomId())
                .toList();

        String today = classroomAvailabilityService.getTodayValue();
        LocalTime now = LocalTime.now();

        List<Schedule> schedules = classroomIds.isEmpty()
                ? List.of()
                : scheduleRepository.findByClassroom_ClassroomIdInAndDayOfWeekOrderByStartTimeAsc(
                        classroomIds,
                        today
                );

        Map<Long, List<Schedule>> scheduleMap = schedules.stream()
                .collect(Collectors.groupingBy(schedule -> schedule.getClassroom().getClassroomId()));

        List<FavoriteClassroomListResponseDto.FavoriteClassroomDto> classrooms = favorites.stream()
                .map(favorite -> {
                    Classroom classroom = favorite.getClassroom();

                    List<Schedule> classroomSchedules = scheduleMap.getOrDefault(
                            classroom.getClassroomId(),
                            List.of()
                    );

                    ClassroomAvailability availability = classroomAvailabilityService.calculateAvailability(
                            classroomSchedules,
                            now
                    );

                    return FavoriteClassroomListResponseDto.FavoriteClassroomDto.builder()
                            .classroomId(classroom.getClassroomId())
                            .buildingName(classroom.getBuilding().getBuildingName())
                            .roomName(classroom.getRoomName())
                            .floor(classroom.getFloor())
                            .hasOutlet(classroom.getHasOutlet())
                            .isFavorite(true)
                            .availabilityStatus(availability.getStatus())
                            .availableMinutes(availability.getAvailableMinutes())
                            .nextClassStartTime(
                                    classroomAvailabilityService.formatTime(availability.getNextClassStartTime())
                            )
                            .build();
                })
                .toList();

        return FavoriteClassroomListResponseDto.builder()
                .classrooms(classrooms)
                .build();
    }

    /**
     * 로그인 사용자가 특정 강의실을 즐겨찾기에 추가한다.
     */
    @Transactional
    public FavoriteStatusResponseDto addFavorite(
            Long userId,
            AddFavoriteRequestDto request
    ) {
        Long classroomId = request.getClassroomId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의실입니다."));

        if (favoriteRepository.existsByUser_UserIdAndClassroom_ClassroomId(userId, classroomId)) {
            return FavoriteStatusResponseDto.builder()
                    .classroomId(classroomId)
                    .isFavorite(true)
                    .build();
        }

        try {
            favoriteRepository.saveAndFlush(Favorite.create(user, classroom));
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 unique constraint가 발생해도 최종 상태는 즐겨찾기 true이므로 성공 응답을 반환한다.
            return FavoriteStatusResponseDto.builder()
                    .classroomId(classroomId)
                    .isFavorite(true)
                    .build();
        }

        return FavoriteStatusResponseDto.builder()
                .classroomId(classroomId)
                .isFavorite(true)
                .build();
    }
}