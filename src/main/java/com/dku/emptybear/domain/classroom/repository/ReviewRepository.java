package com.dku.emptybear.domain.classroom.repository;

import com.dku.emptybear.domain.classroom.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    long countByClassroom_ClassroomId(Long classroomId);

    boolean existsByUser_UserIdAndClassroom_ClassroomId(
            Long userId,
            Long classroomId
    );

    Optional<Review> findByUser_UserIdAndClassroom_ClassroomId(
            Long userId,
            Long classroomId
    );

    Optional<Review> findByReviewIdAndClassroom_ClassroomIdAndUser_UserId(
            Long reviewId,
            Long classroomId,
            Long userId
    );

    @Query("""
            SELECT r
            FROM Review r
            JOIN FETCH r.user u
            WHERE r.classroom.classroomId = :classroomId
            ORDER BY r.createdAt DESC
            """)
    List<Review> findReviewsByClassroomId(
            @Param("classroomId") Long classroomId,
            Pageable pageable
    );
}