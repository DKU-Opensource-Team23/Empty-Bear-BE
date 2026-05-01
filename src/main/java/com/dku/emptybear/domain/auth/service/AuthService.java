package com.emptybear.domain.auth.service;

import com.emptybear.domain.auth.dto.request.SignupRequestDto;
import com.emptybear.domain.auth.dto.response.SignupResponseDto;
import com.emptybear.domain.user.entity.User;
import com.emptybear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}