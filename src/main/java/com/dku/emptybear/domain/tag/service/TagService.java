package com.dku.emptybear.domain.tag.service;

import com.dku.emptybear.domain.tag.dto.response.ReviewTagListResponseDto;
import com.dku.emptybear.domain.tag.entity.Tag;
import com.dku.emptybear.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    /**
     * 리뷰 작성 시 선택 가능한 태그 목록을 조회한다.
     */
    public ReviewTagListResponseDto getReviewTags() {
        List<Tag> tags = tagRepository.findAllByOrderByTagIdAsc();

        return ReviewTagListResponseDto.from(tags);
    }
}