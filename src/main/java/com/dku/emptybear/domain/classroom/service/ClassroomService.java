package com.dku.emptybear.domain.classroom.service;

import com.dku.emptybear.domain.classroom.dto.request.CreateReviewRequestDto;
import com.dku.emptybear.domain.classroom.dto.response.ClassroomDetailResponseDto;
import com.dku.emptybear.domain.classroom.dto.response.ClassroomOverviewListResponseDto;
import com.dku.emptybear.domain.classroom.dto.response.ClassroomWeeklyScheduleResponseDto;
import com.dku.emptybear.domain.classroom.dto.response.CreateReviewResponseDto;
import com.dku.emptybear.domain.classroom.dto.response.ClassroomReviewListResponseDto;
import com.dku.emptybear.domain.classroom.dto.response.DeleteReviewResponseDto;
import com.dku.emptybear.domain.classroom.entity.Classroom;
import com.dku.emptybear.domain.classroom.entity.Schedule;
import com.dku.emptybear.domain.classroom.entity.Review;
import com.dku.emptybear.domain.classroom.entity.ReviewTag;
import com.dku.emptybear.domain.classroom.repository.ClassroomRepository;
import com.dku.emptybear.domain.classroom.repository.ReviewRepository;
import com.dku.emptybear.domain.classroom.repository.ReviewTagRepository;
import com.dku.emptybear.domain.classroom.repository.ScheduleRepository;
import com.dku.emptybear.domain.classroom.service.ClassroomAvailabilityService.ClassroomAvailability;

import com.dku.emptybear.domain.tag.entity.Tag;
import com.dku.emptybear.domain.tag.repository.TagRepository;

import com.dku.emptybear.domain.user.entity.User;
import com.dku.emptybear.domain.user.repository.UserRepository;

import com.dku.emptybear.domain.favorite.entity.Favorite;
import com.dku.emptybear.domain.favorite.repository.FavoriteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassroomService {

    private static final int DEFAULT_REVIEW_LIMIT = 10;
    private static final int MAX_REVIEW_LIMIT = 50;

    private final ClassroomRepository classroomRepository;
    private final ScheduleRepository scheduleRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ClassroomAvailabilityService classroomAvailabilityService;

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

        List<ClassroomOverviewListResponseDto.ClassroomOverviewDto> result = classrooms.stream()
                .map(classroom -> {
                    List<Schedule> classroomSchedules = scheduleMap.getOrDefault(
                            classroom.getClassroomId(),
                            List.of()
                    );

                    ClassroomAvailability availability = classroomAvailabilityService.calculateAvailability(
                            classroomSchedules,
                            now
                    );

                    return ClassroomOverviewListResponseDto.ClassroomOverviewDto.builder()
                            .classroomId(classroom.getClassroomId())
                            .buildingName(classroom.getBuilding().getBuildingName())
                            .roomName(classroom.getRoomName())
                            .floorLabel(toFloorLabel(classroom.getFloor()))
                            .hasOutlet(classroom.getHasOutlet())
                            .isFavorite(favoriteClassroomIds.contains(classroom.getClassroomId()))
                            .availabilityStatus(availability.getStatus())
                            .availableMinutes(availability.getAvailableMinutes())
                            .nextClassStartTime(classroomAvailabilityService.formatTime(availability.getNextClassStartTime()))
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
     * 특정 강의실의 상세 정보, 현재 사용 상태, 즐겨찾기 여부, 리뷰 요약 정보를 조회한다.
     */
    public ClassroomDetailResponseDto getClassroomDetail(Long userId, Long classroomId) {
        Classroom classroom = classroomRepository.findByIdWithBuilding(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의실입니다."));

        boolean isFavorite = favoriteRepository.existsByUser_UserIdAndClassroom_ClassroomId(
                userId,
                classroomId
        );

        List<Schedule> todaySchedules = scheduleRepository.findByClassroom_ClassroomIdAndDayOfWeekOrderByStartTimeAsc(
                classroomId,
                classroomAvailabilityService.getTodayValue()
        );

        ClassroomAvailability availability = classroomAvailabilityService.calculateAvailability(
                todaySchedules,
                LocalTime.now()
        );

        int totalReviewCount = Math.toIntExact(
                reviewRepository.countByClassroom_ClassroomId(classroomId)
        );

        List<ClassroomDetailResponseDto.TagSummaryDto> tagSummaries =
                reviewTagRepository.countTagsByClassroomId(classroomId)
                        .stream()
                        .map(tagCount -> ClassroomDetailResponseDto.TagSummaryDto.builder()
                                .tagId(tagCount.getTagId())
                                .code(tagCount.getCode())
                                .displayName(tagCount.getDisplayName())
                                .count(Math.toIntExact(tagCount.getCount()))
                                .build())
                        .toList();

        return ClassroomDetailResponseDto.builder()
                .classroom(ClassroomDetailResponseDto.ClassroomDetailDto.builder()
                        .classroomId(classroom.getClassroomId())
                        .building(ClassroomDetailResponseDto.BuildingInfoDto.builder()
                                .buildingId(classroom.getBuilding().getBuildingId())
                                .buildingName(classroom.getBuilding().getBuildingName())
                                .build())
                        .roomName(classroom.getRoomName())
                        .floorValue(classroom.getFloor())
                        .floorLabel(toFloorLabel(classroom.getFloor()))
                        .hasOutlet(classroom.getHasOutlet())
                        .isFavorite(isFavorite)
                        .availabilityStatus(availability.getStatus())
                        .availableMinutes(availability.getAvailableMinutes())
                        .nextClassStartTime(classroomAvailabilityService.formatTime(availability.getNextClassStartTime()))
                        .reviewSummary(ClassroomDetailResponseDto.ReviewSummaryDto.builder()
                                .totalReviewCount(totalReviewCount)
                                .tags(tagSummaries)
                                .build())
                        .build())
                .build();
    }

    /**
     * 특정 강의실의 주간 시간표를 조회한다.
     */
    public ClassroomWeeklyScheduleResponseDto getWeeklySchedule(Long classroomId) {
        classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의실입니다."));

        List<Schedule> schedules = scheduleRepository.findByClassroom_ClassroomId(classroomId);

        List<ClassroomWeeklyScheduleResponseDto.ScheduleInfoDto> weeklySchedule = schedules.stream()
                // dayOfWeek는 문자열이므로 MON~SUN 순서가 보장되도록 직접 정렬한다.
                .sorted(
                        Comparator.comparingInt((Schedule schedule) -> getDayOfWeekOrder(schedule.getDayOfWeek()))
                                .thenComparing(Schedule::getStartTime)
                )
                .map(schedule -> ClassroomWeeklyScheduleResponseDto.ScheduleInfoDto.builder()
                        .scheduleId(schedule.getScheduleId())
                        .dayOfWeek(schedule.getDayOfWeek())
                        .startTime(classroomAvailabilityService.formatTime(schedule.getStartTime()))
                        .endTime(classroomAvailabilityService.formatTime(schedule.getEndTime()))
                        .subjectName(schedule.getSubjectName())
                        .build())
                .toList();

        return ClassroomWeeklyScheduleResponseDto.builder()
                .classroomId(classroomId)
                .weeklySchedule(weeklySchedule)
                .build();
    }

    /**
     * 로그인 사용자가 특정 강의실에 태그 기반 리뷰를 작성한다.
     */
    @Transactional
    public CreateReviewResponseDto createReview(
            Long userId,
            Long classroomId,
            CreateReviewRequestDto request
    ) {
        validateTagIds(request.getTagIds());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의실입니다."));

        if (reviewRepository.existsByUser_UserIdAndClassroom_ClassroomId(userId, classroomId)) {
            throw new IllegalArgumentException("이미 해당 강의실에 리뷰를 작성했습니다.");
        }

        List<Long> distinctTagIds = request.getTagIds().stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(LinkedHashSet::new),
                        List::copyOf
                ));

        // 중복 태그 ID가 들어오면 review_tag 중복 매핑을 방지하기 위해 요청을 거절한다.
        if (distinctTagIds.size() != request.getTagIds().size()) {
            throw new IllegalArgumentException("중복된 태그가 포함되어 있습니다.");
        }

        List<Tag> tags = tagRepository.findAllById(distinctTagIds);

        if (tags.size() != distinctTagIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 태그가 포함되어 있습니다.");
        }

        Review review;

        try {
            review = reviewRepository.saveAndFlush(Review.create(user, classroom));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 해당 강의실에 리뷰를 작성했습니다.");
        }

        List<ReviewTag> reviewTags = tags.stream()
                .map(tag -> ReviewTag.create(review, tag))
                .toList();

        reviewTagRepository.saveAll(reviewTags);

        return CreateReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .message("리뷰가 등록되었습니다.")
                .build();
    }

    /**
     * 특정 강의실에 작성된 리뷰 목록과 로그인 사용자의 리뷰 ID를 조회한다.
     */
    public ClassroomReviewListResponseDto getClassroomReviews(
            Long userId,
            Long classroomId,
            Integer limit
    ) {
        classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의실입니다."));

        int reviewLimit = resolveReviewLimit(limit);

        Pageable pageable = PageRequest.of(0, reviewLimit);

        List<Review> reviews = reviewRepository.findReviewsByClassroomId(classroomId, pageable);

        Optional<Review> myReview = reviewRepository.findByUser_UserIdAndClassroom_ClassroomId(
                userId,
                classroomId
        );

        List<Long> reviewIds = reviews.stream()
                .map(Review::getReviewId)
                .toList();

        Map<Long, List<ReviewTag>> reviewTagMap = getReviewTagMap(reviewIds);

        List<ClassroomReviewListResponseDto.ReviewInfoDto> reviewDtos = reviews.stream()
                .map(review -> ClassroomReviewListResponseDto.ReviewInfoDto.builder()
                        .reviewId(review.getReviewId())
                        .user(ClassroomReviewListResponseDto.ReviewUserDto.builder()
                                .userId(review.getUser().getUserId())
                                .nickname(review.getUser().getNickname())
                                .build())
                        .tags(toReviewTagDtos(reviewTagMap.getOrDefault(
                                review.getReviewId(),
                                List.of()
                        )))
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();

        return ClassroomReviewListResponseDto.builder()
                .classroomId(classroomId)
                .myReviewId(myReview.map(Review::getReviewId).orElse(null))
                .reviews(reviewDtos)
                .build();
    }
    
    /**
     * 로그인 사용자가 자신이 작성한 강의실 리뷰를 삭제한다.
     */
    @Transactional
    public DeleteReviewResponseDto deleteReview(
            Long userId,
            Long classroomId,
            Long reviewId
    ) {
        Review review = reviewRepository.findByReviewIdAndClassroom_ClassroomIdAndUser_UserId(
                reviewId,
                classroomId,
                userId
        ).orElseThrow(() -> new IllegalArgumentException("삭제할 수 있는 리뷰가 존재하지 않습니다."));

        // review_tag가 review를 참조하므로 매핑 데이터를 먼저 삭제한다.
        reviewTagRepository.deleteByReview_ReviewId(reviewId);

        reviewRepository.delete(review);

        return DeleteReviewResponseDto.builder()
                .reviewId(reviewId)
                .classroomId(classroomId)
                .build();
    }

    /**
     * limit 값이 없으면 기본 조회 개수를 사용하고, 0 이하 또는 최대값 초과이면 예외를 발생시킨다.
     */
    private int resolveReviewLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_REVIEW_LIMIT;
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("limit은 1 이상이어야 합니다.");
        }

        if (limit > MAX_REVIEW_LIMIT) {
            throw new IllegalArgumentException("limit은 최대 " + MAX_REVIEW_LIMIT + "까지 가능합니다.");
        }

        return limit;
    }

    /**
     * 리뷰 ID 목록에 해당하는 태그들을 reviewId 기준으로 묶는다.
     */
    private Map<Long, List<ReviewTag>> getReviewTagMap(List<Long> reviewIds) {
        if (reviewIds.isEmpty()) {
            return Map.of();
        }

        return reviewTagRepository.findByReviewIdsWithTag(reviewIds)
                .stream()
                .collect(Collectors.groupingBy(reviewTag -> reviewTag.getReview().getReviewId()));
    }

    /**
     * ReviewTag 엔티티 목록을 리뷰 응답용 태그 DTO로 변환한다.
     */
    private List<ClassroomReviewListResponseDto.ReviewTagDto> toReviewTagDtos(
            List<ReviewTag> reviewTags
    ) {
        return reviewTags.stream()
                .map(reviewTag -> ClassroomReviewListResponseDto.ReviewTagDto.builder()
                        .tagId(reviewTag.getTag().getTagId())
                        .code(reviewTag.getTag().getCode())
                        .displayName(reviewTag.getTag().getDisplayName())
                        .build())
                .toList();
    }

    /**
     * 리뷰 작성 요청의 태그 ID 목록을 검증한다.
     */
    private void validateTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            throw new IllegalArgumentException("태그를 1개 이상 선택해야 합니다.");
        }

        if (tagIds.stream().anyMatch(tagId -> tagId == null || tagId <= 0)) {
            throw new IllegalArgumentException("유효하지 않은 태그 ID가 포함되어 있습니다.");
        }
    }

    /**
     * 요일 문자열을 주간 정렬 순서로 변환한다.
     */
    private int getDayOfWeekOrder(String dayOfWeek) {
        if (dayOfWeek == null) {
            return 99;
        }

        return switch (dayOfWeek) {
            case "MON" -> 1;
            case "TUE" -> 2;
            case "WED" -> 3;
            case "THU" -> 4;
            case "FRI" -> 5;
            case "SAT" -> 6;
            case "SUN" -> 7;
            default -> 99;
        };
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
}