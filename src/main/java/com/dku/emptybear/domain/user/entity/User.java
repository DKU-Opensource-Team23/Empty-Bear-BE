package com.dku.emptybear.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_id", nullable = false, length = 50, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, length = 50, unique = true)
    private String nickname;

    @Column(name = "student_number", nullable = false, length = 20, unique = true)
    private String studentNumber;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Builder
    public User(String loginId, String password, String nickname, String studentNumber, String department) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.studentNumber = studentNumber;
        this.department = department;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }

    public void updateProfile(String nickname, String studentNumber, String department) {
        if (nickname != null) {
            this.nickname = nickname;
        }

        if (studentNumber != null) {
            this.studentNumber = studentNumber;
        }

        if (department != null) {
            this.department = department;
        }
    }
}