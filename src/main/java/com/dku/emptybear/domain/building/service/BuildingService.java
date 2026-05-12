package com.dku.emptybear.domain.building.service;

import com.dku.emptybear.domain.building.dto.response.BuildingListResponseDto;
import com.dku.emptybear.domain.building.dto.response.FloorListResponseDto;
import com.dku.emptybear.domain.building.entity.Building;
import com.dku.emptybear.domain.building.entity.FloorPlan;
import com.dku.emptybear.domain.building.repository.BuildingRepository;
import com.dku.emptybear.domain.building.repository.FloorPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final FloorPlanRepository floorPlanRepository;

    public BuildingListResponseDto getBuildings() {
        List<Building> buildings = buildingRepository.findAll();

        return BuildingListResponseDto.from(buildings);
    }

    public FloorListResponseDto getFloors(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 건물입니다."));

        List<FloorPlan> floorPlans = floorPlanRepository.findByBuilding_BuildingIdOrderByFloorAsc(buildingId);

        return FloorListResponseDto.of(building, floorPlans);
    }
}