package com.dku.emptybear.domain.recommend.controller;

import com.dku.emptybear.domain.recommend.dto.request.RecommendRequestDto;
import com.dku.emptybear.domain.recommend.dto.response.RecommendClassroomResponseDto;
import com.dku.emptybear.domain.recommend.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Recommend", description = "강의실 추천 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    @Operation(
            summary = "추천 강의실 목록 조회",
            description = "현재 시간표 기준으로 사용 가능한 강의실을 조회하고, 요청 조건과 추천 점수에 따라 정렬합니다."
    )
    @GetMapping("/classrooms")
    public ResponseEntity<RecommendClassroomResponseDto> getRecommendedClassrooms(
            Authentication authentication,

            @Parameter(description = "추천 조건")
            @Valid @ModelAttribute RecommendRequestDto request
    ) {
        Long userId = authentication == null
                ? null
                : Long.valueOf(authentication.getName());

        RecommendClassroomResponseDto result =
                recommendService.recommendClassrooms(userId, request);

        return ResponseEntity.ok(result);
    }
}
