package com.dku.emptybear.domain.classroom.repository;

import com.dku.emptybear.domain.classroom.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser_UserIdAndClassroom_ClassroomIdIn(
            Long userId,
            Collection<Long> classroomIds
    );

    boolean existsByUser_UserIdAndClassroom_ClassroomId(
            Long userId,
            Long classroomId
    );
}