package com.dku.emptybear.domain.building.controller;

import com.dku.emptybear.domain.building.dto.response.BuildingListResponseDto;
import com.dku.emptybear.domain.building.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Buildings", description = "건물 관련 API")
@RestController
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @Operation(
            summary = "건물 목록 조회",
            description = "서비스에서 조회 가능한 건물 목록을 반환합니다. 추천 조건 선택 및 지도 화면의 건물 선택에 사용됩니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/buildings")
    public BuildingListResponseDto getBuildings() {
        return buildingService.getBuildings();
    }
}