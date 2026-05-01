package com.dku.emptybear.domain.auth.controller;

import com.dku.emptybear.domain.auth.dto.request.SignupRequestDto;
import com.dku.emptybear.domain.auth.dto.response.SignupResponseDto;
import com.dku.emptybear.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}