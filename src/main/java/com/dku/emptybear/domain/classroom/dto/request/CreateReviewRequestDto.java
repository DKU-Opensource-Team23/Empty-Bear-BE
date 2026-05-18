package com.dku.emptybear.domain.classroom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateReviewRequestDto {

    @Schema(description = "리뷰에 선택한 태그 ID 목록", example = "[1, 2]")
    @NotEmpty(message = "태그를 1개 이상 선택해야 합니다.")
    private List<Long> tagIds;
}