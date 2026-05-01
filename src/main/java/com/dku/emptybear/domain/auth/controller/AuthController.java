package com.dku.emptybear.domain.auth.controller;

import com.dku.emptybear.domain.auth.dto.request.LoginRequestDto;
import com.dku.emptybear.domain.auth.dto.request.SignupRequestDto;
import com.dku.emptybear.domain.auth.dto.request.LogoutRequestDto;
import com.dku.emptybear.domain.auth.dto.response.LoginResponseDto;
import com.dku.emptybear.domain.auth.dto.response.SignupResponseDto;
import com.dku.emptybear.domain.auth.dto.response.AuthMessageResponseDto;
import com.dku.emptybear.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "로그인 아이디, 비밀번호, 닉네임, 학번, 학과를 입력받아 새로운 사용자를 등록합니다."
    )
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignupResponseDto signup(@Valid @RequestBody SignupRequestDto request) {
        return authService.signup(request);
    }

    @Operation(
            summary = "로그인",
            description = "사용자 식별 정보와 비밀번호를 입력받아 로그인하고, 인증 토큰 및 사용자 기본 정보를 반환합니다."
    )
    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    @Operation(
            summary = "로그아웃",
            description = "로그인된 사용자의 로그아웃을 처리하고, 리프레시 토큰을 무효화합니다."
    )
    @PostMapping("/logout")
    public AuthMessageResponseDto logout(
            @Parameter(
                description = "액세스 토큰",
                example = "Bearer access-token-example",
                in = ParameterIn.HEADER
            )
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody LogoutRequestDto request
    ) {
        return authService.logout(authorizationHeader, request);
    }
}