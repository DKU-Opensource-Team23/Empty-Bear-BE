package com.dku.emptybear.domain.tag.controller;

import com.dku.emptybear.domain.tag.dto.response.ReviewTagListResponseDto;
import com.dku.emptybear.domain.tag.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tags", description = "태그 관련 API")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(
            summary = "리뷰 태그 목록 조회",
            description = "강의실 리뷰 작성 시 선택 가능한 태그 목록을 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/reviews")
    public ReviewTagListResponseDto getReviewTags() {
        return tagService.getReviewTags();
    }
}