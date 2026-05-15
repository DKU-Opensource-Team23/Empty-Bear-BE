package com.dku.emptybear.domain.tag.dto.response;

import com.dku.emptybear.domain.tag.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewTagListResponseDto {

    @Schema(description = "리뷰 태그 목록")
    private List<ReviewTagInfoDto> tags;

    @Getter
    @Builder
    public static class ReviewTagInfoDto {

        @Schema(description = "태그 고유 ID", example = "1")
        private Long tagId;

        @Schema(description = "태그 내부 코드값", example = "QUIET")
        private String code;

        @Schema(description = "사용자 표시용 태그명", example = "조용해요")
        private String displayName;
    }

    public static ReviewTagListResponseDto from(List<Tag> tags) {
        return ReviewTagListResponseDto.builder()
                .tags(
                        tags.stream()
                                .map(tag -> ReviewTagInfoDto.builder()
                                        .tagId(tag.getTagId())
                                        .code(tag.getCode())
                                        .displayName(tag.getDisplayName())
                                        .build())
                                .toList()
                )
                .build();
    }
}