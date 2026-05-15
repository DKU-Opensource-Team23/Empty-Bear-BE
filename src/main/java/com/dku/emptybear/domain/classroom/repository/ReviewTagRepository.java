package com.dku.emptybear.domain.classroom.repository;

import com.dku.emptybear.domain.classroom.entity.ReviewTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {

    @Query("""
            SELECT
                t.tagId AS tagId,
                t.code AS code,
                t.displayName AS displayName,
                COUNT(rt.reviewTagId) AS count
            FROM ReviewTag rt
            JOIN rt.review r
            JOIN rt.tag t
            WHERE r.classroom.classroomId = :classroomId
            GROUP BY t.tagId, t.code, t.displayName
            ORDER BY COUNT(rt.reviewTagId) DESC, t.tagId ASC
            """)
    List<TagCountProjection> countTagsByClassroomId(@Param("classroomId") Long classroomId);

    interface TagCountProjection {

        Long getTagId();

        String getCode();

        String getDisplayName();

        Long getCount();
    }
}