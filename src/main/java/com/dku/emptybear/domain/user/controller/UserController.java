package com.dku.emptybear.domain.user.controller;

import com.dku.emptybear.domain.user.dto.request.UpdateMyInfoRequestDto;
import com.dku.emptybear.domain.user.dto.response.MyInfoResponseDto;
import com.dku.emptybear.domain.user.dto.response.UpdateMyInfoResponseDto;
import com.dku.emptybear.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Tag(name = "Users", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "내 정보 조회",
            description = "로그인한 사용자의 홈 화면 프로필 카드 표시용 정보를 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public MyInfoResponseDto getMyInfo(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return userService.getMyInfo(userId);
    }

    @Operation(
            summary = "내 정보 수정",
            description = "로그인한 사용자의 프로필 정보인 닉네임, 학번, 학과를 수정합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/me")
    public UpdateMyInfoResponseDto updateMyInfo(
            Authentication authentication,
            @Valid @RequestBody UpdateMyInfoRequestDto request
    ) {
        Long userId = Long.valueOf(authentication.getName());
        return userService.updateMyInfo(userId, request);
    }
}