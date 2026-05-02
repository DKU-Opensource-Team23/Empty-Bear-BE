package com.dku.emptybear.domain.user.controller;

import com.dku.emptybear.domain.user.dto.response.MyInfoResponseDto;
import com.dku.emptybear.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public MyInfoResponseDto getMyInfo(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return userService.getMyInfo(authorizationHeader);
    }
}