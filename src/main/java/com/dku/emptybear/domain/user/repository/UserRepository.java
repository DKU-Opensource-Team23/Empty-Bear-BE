package com.dku.emptybear.domain.user.repository;

import com.dku.emptybear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);

    boolean existsByStudentNumber(String studentNumber);

    Optional<User> findByLoginId(String loginId);
}