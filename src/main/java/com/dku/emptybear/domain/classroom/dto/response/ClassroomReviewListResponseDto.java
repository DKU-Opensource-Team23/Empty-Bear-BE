package com.dku.emptybear.domain.classroom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ClassroomReviewListResponseDto {

    @Schema(description = "강의실 고유 ID", example = "12")
    private Long classroomId;

    @Schema(description = "로그인 사용자가 해당 강의실에 작성한 리뷰 ID", example = "31", nullable = true)
    private Long myReviewId;

    @Schema(description = "리뷰 목록")
    private List<ReviewInfoDto> reviews;

    @Getter
    @Builder
    public static class ReviewInfoDto {

        @Schema(description = "리뷰 고유 ID", example = "31")
        private Long reviewId;

        @Schema(description = "리뷰 작성자 정보")
        private ReviewUserDto user;

        @Schema(description = "리뷰에 선택된 태그 목록")
        private List<ReviewTagDto> tags;

        @Schema(description = "리뷰 작성 시각", example = "2026-04-10T14:30:00")
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class ReviewUserDto {

        @Schema(description = "리뷰 작성자 고유 ID", example = "1")
        private Long userId;

        @Schema(description = "리뷰 작성자 닉네임", example = "비었곰")
        private String nickname;
    }

    @Getter
    @Builder
    public static class ReviewTagDto {

        @Schema(description = "태그 ID", example = "1")
        private Long tagId;

        @Schema(description = "태그 내부 코드값", example = "QUIET")
        private String code;

        @Schema(description = "태그 표시명", example = "조용해요")
        private String displayName;
    }
}