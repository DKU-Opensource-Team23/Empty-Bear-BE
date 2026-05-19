package com.dku.emptybear.domain.favorite.controller;

import com.dku.emptybear.domain.favorite.dto.response.FavoriteClassroomListResponseDto;
import com.dku.emptybear.domain.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}