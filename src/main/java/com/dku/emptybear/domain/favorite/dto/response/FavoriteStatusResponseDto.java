package com.dku.emptybear.domain.favorite.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteStatusResponseDto {

    @Schema(description = "즐겨찾기 처리된 강의실 ID", example = "12")
    private Long classroomId;

    @Schema(description = "즐겨찾기 여부", example = "true")
    private Boolean isFavorite;
}