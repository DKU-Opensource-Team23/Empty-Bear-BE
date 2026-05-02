package com.dku.emptybear.domain.user.service;

import com.dku.emptybear.domain.auth.jwt.JwtTokenProvider;
import com.dku.emptybear.domain.user.dto.response.MyInfoResponseDto;
import com.dku.emptybear.domain.user.entity.User;
import com.dku.emptybear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final String INVALID_TOKEN_MESSAGE = "유효하지 않은 토큰입니다.";

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MyInfoResponseDto getMyInfo(String authorizationHeader) {
        String accessToken = extractAccessToken(authorizationHeader);

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException(INVALID_TOKEN_MESSAGE);
        }

        Long userId = jwtTokenProvider.getUserId(accessToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return MyInfoResponseDto.builder()
                .user(MyInfoResponseDto.UserInfoDto.builder()
                        .userId(user.getUserId())
                        .nickname(user.getNickname())
                        .department(user.getDepartment())
                        .studentNumber(user.getStudentNumber())
                        .build())
                .build();
    }

    private String extractAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException(INVALID_TOKEN_MESSAGE);
        }

        String accessToken = authorizationHeader.trim();

        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7).trim();
        }

        if (accessToken.isBlank()) {
            throw new IllegalArgumentException(INVALID_TOKEN_MESSAGE);
        }

        return accessToken;
    }
}