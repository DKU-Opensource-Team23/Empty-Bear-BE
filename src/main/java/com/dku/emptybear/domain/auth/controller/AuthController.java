package com.emptybear.domain.auth.controller;

import com.emptybear.domain.auth.dto.request.SignupRequestDto;
import com.emptybear.domain.auth.dto.response.SignupResponseDto;
import com.emptybear.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignupResponseDto signup(@Valid @RequestBody SignupRequestDto request) {
        return authService.signup(request);
    }
}