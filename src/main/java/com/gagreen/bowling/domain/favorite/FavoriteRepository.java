package com.gagreen.bowling.domain.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteCenterVo, Long>, JpaSpecificationExecutor<FavoriteCenterVo> {
    boolean existsByCenterIdAndUserId(Long centerId, Long userId);

    Optional<FavoriteCenterVo> findByCenterIdAndUserId(Long centerId, Long userId);

}
