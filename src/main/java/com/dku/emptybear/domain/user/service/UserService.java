package com.dku.emptybear.domain.user.service;

import com.dku.emptybear.domain.auth.jwt.JwtTokenProvider;
import com.dku.emptybear.domain.user.dto.request.UpdateMyInfoRequestDto;
import com.dku.emptybear.domain.user.dto.response.MyInfoResponseDto;
import com.dku.emptybear.domain.user.dto.response.UpdateMyInfoResponseDto;
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
        Long userId = getUserIdFromAuthorizationHeader(authorizationHeader);

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

    @Transactional
    public UpdateMyInfoResponseDto updateMyInfo(
            String authorizationHeader,
            UpdateMyInfoRequestDto request
    ) {
        Long userId = getUserIdFromAuthorizationHeader(authorizationHeader);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        validateUpdateRequest(request);
        validateDuplicateForUpdate(user, request);

        user.updateProfile(
                normalizeNullableText(request.getNickname()),
                normalizeNullableText(request.getStudentNumber()),
                normalizeNullableText(request.getDepartment())
        );

        return UpdateMyInfoResponseDto.builder()
                .user(UpdateMyInfoResponseDto.UserInfoDto.builder()
                        .userId(user.getUserId())
                        .loginId(user.getLoginId())
                        .nickname(user.getNickname())
                        .studentNumber(user.getStudentNumber())
                        .department(user.getDepartment())
                        .build())
                .build();
    }

    private Long getUserIdFromAuthorizationHeader(String authorizationHeader) {
        String accessToken = extractAccessToken(authorizationHeader);

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException(INVALID_TOKEN_MESSAGE);
        }

        return jwtTokenProvider.getUserId(accessToken);
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

    private void validateUpdateRequest(UpdateMyInfoRequestDto request) {
        if (request.getNickname() == null
                && request.getStudentNumber() == null
                && request.getDepartment() == null) {
            throw new IllegalArgumentException("수정할 정보가 없습니다.");
        }

        validateNotBlankIfPresent(request.getNickname(), "닉네임은 빈 값일 수 없습니다.");
        validateNotBlankIfPresent(request.getStudentNumber(), "학번은 빈 값일 수 없습니다.");
        validateNotBlankIfPresent(request.getDepartment(), "학과는 빈 값일 수 없습니다.");
    }

    private void validateNotBlankIfPresent(String value, String message) {
        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateDuplicateForUpdate(User user, UpdateMyInfoRequestDto request) {
        String nickname = normalizeNullableText(request.getNickname());
        String studentNumber = normalizeNullableText(request.getStudentNumber());

        if (nickname != null
                && !nickname.equals(user.getNickname())
                && userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        if (studentNumber != null
                && !studentNumber.equals(user.getStudentNumber())
                && userRepository.existsByStudentNumber(studentNumber)) {
            throw new IllegalArgumentException("이미 등록된 학번입니다.");
        }
    }

    private String normalizeNullableText(String value) {
        if (value == null) {
            return null;
        }

        return value.trim();
    }
}