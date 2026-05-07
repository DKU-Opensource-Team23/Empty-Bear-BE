package com.dku.emptybear.domain.user.entity;

import com.dku.emptybear.domain.building.entity.Building;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "user_preferences")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPreferenceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_building_id")
    private Building preferredBuilding;

    private Integer minAvailableTime;

    private Boolean needOutlet;

    private LocalDateTime updatedAt;
}