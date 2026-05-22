package com.dku.emptybear.domain.favorite.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AddFavoriteRequestDto {

    @Schema(description = "즐겨찾기에 추가할 강의실 ID", example = "12")
    @NotNull(message = "강의실 ID는 필수입니다.")
    private Long classroomId;
}