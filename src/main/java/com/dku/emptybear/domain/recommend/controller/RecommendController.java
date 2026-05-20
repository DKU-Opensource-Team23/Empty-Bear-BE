package com.dku.emptybear.domain.recommend.controller;

import com.dku.emptybear.domain.recommend.dto.request.RecommendRequestDto;
import com.dku.emptybear.domain.recommend.dto.response.RecommendClassroomResponseDto;
import com.dku.emptybear.domain.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/classrooms")
    public ResponseEntity<?> getRecommendedClassrooms(
            @ModelAttribute RecommendRequestDto request
    ) {
        var result =
                recommendService.recommendClassrooms(request);

        return ResponseEntity.ok(result);
    }
}
