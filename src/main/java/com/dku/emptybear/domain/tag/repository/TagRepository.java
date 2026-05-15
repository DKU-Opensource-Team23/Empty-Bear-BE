package com.dku.emptybear.domain.tag.repository;

import com.dku.emptybear.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByOrderByTagIdAsc();
}