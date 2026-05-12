package com.dku.emptybear.domain.building.repository;

import com.dku.emptybear.domain.building.entity.FloorPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloorPlanRepository extends JpaRepository<FloorPlan, Long> {

    List<FloorPlan> findByBuilding_BuildingIdOrderByFloorAsc(Long buildingId);
}