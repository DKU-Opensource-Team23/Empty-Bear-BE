package com.emptybear.domain.user.repository;

import com.emptybear.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);

    boolean existsByStudentNumber(String studentNumber);
}