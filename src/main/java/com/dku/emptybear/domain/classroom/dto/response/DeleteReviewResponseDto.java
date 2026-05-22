package com.dku.emptybear.domain.classroom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteReviewResponseDto {

    @Schema(description = "삭제된 리뷰 ID", example = "31")
    private Long reviewId;

    @Schema(description = "리뷰가 속한 강의실 ID", example = "12")
    private Long classroomId;
}