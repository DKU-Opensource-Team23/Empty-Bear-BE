package com.dku.emptybear.domain.favorite.controller;

import com.dku.emptybear.domain.favorite.dto.request.AddFavoriteRequestDto;
import com.dku.emptybear.domain.favorite.dto.response.FavoriteStatusResponseDto;
import com.dku.emptybear.domain.favorite.dto.response.FavoriteClassroomListResponseDto;
import com.dku.emptybear.domain.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@Tag(name = "Users", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users/me/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "즐겨찾기한 강의실 목록 조회",
            description = "로그인한 사용자가 즐겨찾기한 강의실 목록을 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public FavoriteClassroomListResponseDto getFavoriteClassrooms(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return favoriteService.getFavoriteClassrooms(userId);
    }

    @Operation(
            summary = "즐겨찾기 추가",
            description = "로그인한 사용자가 특정 강의실을 즐겨찾기에 추가합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public FavoriteStatusResponseDto addFavorite(
            Authentication authentication,
            @Valid @RequestBody AddFavoriteRequestDto request
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return favoriteService.addFavorite(userId, request);
    }

    @Operation(
            summary = "즐겨찾기 삭제",
            description = "로그인한 사용자의 즐겨찾기 목록에서 특정 강의실을 삭제합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{classroomId}")
    public FavoriteStatusResponseDto deleteFavorite(
            Authentication authentication,

            @Parameter(description = "즐겨찾기에서 삭제할 강의실 ID", example = "12")
            @PathVariable Long classroomId
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return favoriteService.deleteFavorite(userId, classroomId);
    }
}