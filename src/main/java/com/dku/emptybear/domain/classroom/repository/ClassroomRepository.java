package com.dku.emptybear.domain.classroom.repository;

import com.dku.emptybear.domain.classroom.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    List<Classroom> findByBuilding_BuildingIdAndFloorOrderByRoomNameAsc(
            Long buildingId,
            Integer floor
    );

    @Query("""
            SELECT c
            FROM Classroom c
            JOIN FETCH c.building b
            WHERE (:buildingId IS NULL OR b.buildingId = :buildingId)
              AND (:floorValue IS NULL OR c.floor = :floorValue)
              AND (:hasOutlet IS NULL OR c.hasOutlet = :hasOutlet)
            ORDER BY b.buildingName ASC, c.floor ASC, c.roomName ASC
            """)
    List<Classroom> findClassroomsByFilters(
            @Param("buildingId") Long buildingId,
            @Param("floorValue") Integer floorValue,
            @Param("hasOutlet") Boolean hasOutlet
    );
}