package com.dku.emptybear.domain.auth.service;

import com.dku.emptybear.domain.auth.dto.request.LoginRequestDto;
import com.dku.emptybear.domain.auth.dto.request.LogoutRequestDto;
import com.dku.emptybear.domain.auth.dto.request.SignupRequestDto;
import com.dku.emptybear.domain.auth.dto.response.AuthMessageResponseDto;
import com.dku.emptybear.domain.auth.dto.response.LoginResponseDto;
import com.dku.emptybear.domain.auth.dto.response.SignupResponseDto;
import com.dku.emptybear.domain.auth.jwt.JwtTokenProvider;
import com.dku.emptybear.domain.user.entity.User;
import com.dku.emptybear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public SignupResponseDto signup(SignupRequestDto request) {
        validateDuplicate(request);

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .studentNumber(request.getStudentNumber())
                .department(request.getDepartment())
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponseDto.builder()
                .user(SignupResponseDto.UserInfoDto.builder()
                        .userId(savedUser.getUserId())
                        .loginId(savedUser.getLoginId())
                        .build())
                .message("회원가입에 성공했습니다.")
                .build();
    }

    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        user.updateRefreshToken(refreshToken);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(LoginResponseDto.UserInfoDto.builder()
                        .userId(user.getUserId())
                        .loginId(user.getLoginId())
                        .nickname(user.getNickname())
                        .studentNumber(user.getStudentNumber())
                        .department(user.getDepartment())
                        .build())
                .build();
    }

    public AuthMessageResponseDto logout(String authorizationHeader, LogoutRequestDto request) {
        String accessToken = extractToken(authorizationHeader);
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        Long accessTokenUserId = jwtTokenProvider.getUserId(accessToken);
        Long refreshTokenUserId = jwtTokenProvider.getUserId(refreshToken);

        if (!Objects.equals(accessTokenUserId, refreshTokenUserId)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        User user = userRepository.findById(accessTokenUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.getRefreshToken() == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        if (!user.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        user.clearRefreshToken();

        return AuthMessageResponseDto.builder()
                .message("로그아웃에 성공했습니다.")
                .build();
    }

    private void validateDuplicate(SignupRequestDto request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 로그인 아이디입니다.");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        if (userRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new IllegalArgumentException("이미 등록된 학번입니다.");
        }
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        return authorizationHeader.substring(7);
    }
}