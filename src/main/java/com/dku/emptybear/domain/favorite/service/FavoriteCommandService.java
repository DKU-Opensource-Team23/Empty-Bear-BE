package com.dku.emptybear.domain.favorite.service;

import com.dku.emptybear.domain.classroom.entity.Classroom;
import com.dku.emptybear.domain.favorite.entity.Favorite;
import com.dku.emptybear.domain.favorite.repository.FavoriteRepository;
import com.dku.emptybear.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteCommandService {

    private final FavoriteRepository favoriteRepository;

    /**
     * 즐겨찾기 추가 시 unique constraint 충돌이 발생해도 외부 트랜잭션이 rollback-only로 오염되지 않도록 별도 트랜잭션에서 저장한다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addFavoriteInNewTransaction(User user, Classroom classroom) {
        favoriteRepository.saveAndFlush(Favorite.create(user, classroom));
    }
}