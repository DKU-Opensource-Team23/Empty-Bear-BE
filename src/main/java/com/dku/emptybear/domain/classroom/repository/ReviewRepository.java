package com.dku.emptybear.domain.classroom.repository;

import com.dku.emptybear.domain.classroom.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    long countByClassroom_ClassroomId(Long classroomId);

    boolean existsByUser_UserIdAndClassroom_ClassroomId(
            Long userId,
            Long classroomId
    );
}