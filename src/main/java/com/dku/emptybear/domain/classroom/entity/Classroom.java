package com.dku.emptybear.domain.classroom.entity;

import com.dku.emptybear.domain.building.entity.Building;
import com.dku.emptybear.domain.building.entity.FloorPlan;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "classroom")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id")
    private Long classroomId;

    @Column(name = "room_name", nullable = false, length = 50)
    private String roomName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_plan_id", nullable = false)
    private FloorPlan floorPlan;

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @Column(name = "has_outlet", nullable = false)
    private Boolean hasOutlet;

    @Column(name = "description")
    private String description;

    @Column(name = "map_x", precision = 5, scale = 4)
    private BigDecimal mapX;

    @Column(name = "map_y", precision = 5, scale = 4)
    private BigDecimal mapY;
}