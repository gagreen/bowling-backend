package com.gagreen.bowling.domain.favorite;

import com.gagreen.bowling.domain.user.UserVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteVo, Long>, JpaSpecificationExecutor<FavoriteVo> {
    boolean existsByCenterIdAndUserId(Long centerId, Long userId);

    Optional<FavoriteVo> findByCenterIdAndUser(Long centerId, UserVo user);

}
