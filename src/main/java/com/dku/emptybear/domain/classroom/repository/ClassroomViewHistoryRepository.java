package com.dku.emptybear.domain.classroom.repository;

import com.dku.emptybear.domain.classroom.entity.ClassroomViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassroomViewHistoryRepository extends JpaRepository<ClassroomViewHistory, Long> {

    Optional<ClassroomViewHistory> findByUser_UserIdAndClassroom_ClassroomId(
            Long userId,
            Long classroomId
    );

    @Query("""
            SELECT cvh
            FROM ClassroomViewHistory cvh
            JOIN FETCH cvh.classroom c
            JOIN FETCH c.building b
            WHERE cvh.user.userId = :userId
            ORDER BY cvh.viewedAt DESC
            """)
    List<ClassroomViewHistory> findByUserIdWithClassroomAndBuildingOrderByViewedAtDesc(
            @Param("userId") Long userId
    );
}
