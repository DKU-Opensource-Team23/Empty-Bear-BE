package com.dku.emptybear.domain.classroom.controller;

import com.dku.emptybear.domain.classroom.dto.response.ClassroomDetailResponseDto;
import com.dku.emptybear.domain.classroom.dto.response.ClassroomOverviewListResponseDto;
import com.dku.emptybear.domain.classroom.service.ClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Classrooms", description = "강의실 관련 API")
@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @Operation(
            summary = "강의실 개요 목록 조회",
            description = "건물, 층, 콘센트 여부, 사용 가능 상태 등의 조건에 맞는 강의실 개요 카드 목록을 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ClassroomOverviewListResponseDto getClassroomOverviews(
            Authentication authentication,

            @Parameter(description = "조회할 건물 ID", example = "1")
            @RequestParam(required = false) Long buildingId,

            @Parameter(description = "조회할 층의 내부 값", example = "5")
            @RequestParam(required = false) Integer floorValue,

            @Parameter(description = "콘센트 여부 필터", example = "true")
            @RequestParam(required = false) Boolean hasOutlet,

            @Parameter(description = "현재 사용 상태 필터", example = "AVAILABLE_LONG")
            @RequestParam(required = false) String availabilityStatus,

            @Parameter(description = "최소 사용 가능 시간(분) 필터", example = "30")
            @RequestParam(required = false) Integer minAvailableTime
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return classroomService.getClassroomOverviews(
                userId,
                buildingId,
                floorValue,
                hasOutlet,
                availabilityStatus,
                minAvailableTime
        );
    }

    @Operation(
            summary = "강의실 상세 정보 조회",
            description = "특정 강의실의 상세 정보, 현재 사용 상태, 리뷰 요약, 건물 위치 정보를 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{classroomId}")
    public ClassroomDetailResponseDto getClassroomDetail(
            Authentication authentication,

            @Parameter(description = "상세 정보를 조회할 강의실 ID", example = "12")
            @PathVariable Long classroomId
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return classroomService.getClassroomDetail(userId, classroomId);
    }
}