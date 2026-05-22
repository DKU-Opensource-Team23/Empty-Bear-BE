package com.dku.emptybear.domain.favorite.repository;

import com.dku.emptybear.domain.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser_UserIdAndClassroom_ClassroomIdIn(
            Long userId,
            Collection<Long> classroomIds
    );

    boolean existsByUser_UserIdAndClassroom_ClassroomId(
            Long userId,
            Long classroomId
    );

    Optional<Favorite> findByUser_UserIdAndClassroom_ClassroomId(
            Long userId,
            Long classroomId
    );

    @Query("""
            SELECT f
            FROM Favorite f
            JOIN FETCH f.classroom c
            JOIN FETCH c.building b
            WHERE f.user.userId = :userId
            ORDER BY f.createdAt DESC
            """)
    List<Favorite> findFavoritesByUserIdWithClassroomAndBuilding(@Param("userId") Long userId);
}