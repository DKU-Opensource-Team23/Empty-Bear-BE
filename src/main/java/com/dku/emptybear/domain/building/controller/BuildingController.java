package com.dku.emptybear.domain.building.controller;

import com.dku.emptybear.domain.building.dto.response.BuildingListResponseDto;
import com.dku.emptybear.domain.building.dto.response.FloorListResponseDto;
import com.dku.emptybear.domain.building.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Buildings", description = "건물 관련 API")
@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @Operation(
            summary = "건물 목록 조회",
            description = "로그인한 사용자가 서비스에서 조회 가능한 건물 목록을 조회합니다. 추천 조건 선택 및 지도 화면의 건물 선택에 사용됩니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public BuildingListResponseDto getBuildings() {
        return buildingService.getBuildings();
    }

    @Operation(
            summary = "층 목록 조회",
            description = "특정 건물에서 조회 가능한 층 목록을 반환합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{buildingId}/floors")
    public FloorListResponseDto getFloors(
            @Parameter(description = "층 목록을 조회할 건물 ID", example = "1")
            @PathVariable Long buildingId
    ) {
        return buildingService.getFloors(buildingId);
    }
}