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
    @Column(name = "user_preference_id")
    private Long userPreferenceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_building_id")
    private Building preferredBuilding;

    @Column(name = "min_available_time")
    private Integer minAvailableTime;

    @Column(name = "need_outlet")
    private Boolean needOutlet;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static UserPreference create(
            User user,
            Building preferredBuilding,
            Integer minAvailableTime,
            Boolean needOutlet
    ) {
        UserPreference userPreference = new UserPreference();
        userPreference.user = user;
        userPreference.preferredBuilding = preferredBuilding;
        userPreference.minAvailableTime = minAvailableTime;
        userPreference.needOutlet = needOutlet;
        return userPreference;
    }

    public void update(
            Building preferredBuilding,
            Integer minAvailableTime,
            Boolean needOutlet
    ) {
        if (preferredBuilding != null) {
            this.preferredBuilding = preferredBuilding;
        }

        if (minAvailableTime != null) {
            this.minAvailableTime = minAvailableTime;
        }

        if (needOutlet != null) {
            this.needOutlet = needOutlet;
        }
    }

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}