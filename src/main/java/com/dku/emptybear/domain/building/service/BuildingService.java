package com.dku.emptybear.domain.building.service;

import com.dku.emptybear.domain.building.dto.response.BuildingListResponseDto;
import com.dku.emptybear.domain.building.entity.Building;
import com.dku.emptybear.domain.building.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public BuildingListResponseDto getBuildings() {
        List<Building> buildings = buildingRepository.findAll();

        return BuildingListResponseDto.from(buildings);
    }
}