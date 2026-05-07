package com.dku.emptybear.domain.user.repository;

import com.dku.emptybear.domain.user.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    Optional<UserPreference> findByUser_UserId(Long userId);
}