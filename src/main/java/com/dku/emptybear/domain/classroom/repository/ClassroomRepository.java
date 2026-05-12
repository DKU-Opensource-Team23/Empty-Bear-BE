package com.dku.emptybear.domain.classroom.repository;

import com.dku.emptybear.domain.classroom.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    List<Classroom> findByBuilding_BuildingIdAndFloorOrderByRoomNameAsc(
            Long buildingId,
            Integer floor
    );
}