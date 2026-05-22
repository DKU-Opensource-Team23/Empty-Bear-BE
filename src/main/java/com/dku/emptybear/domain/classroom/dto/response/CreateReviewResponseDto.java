package com.dku.emptybear.domain.classroom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateReviewResponseDto {

    @Schema(description = "생성된 리뷰 고유 ID", example = "31")
    private Long reviewId;

    @Schema(description = "리뷰 작성 결과 메시지", example = "리뷰가 등록되었습니다.")
    private String message;
}