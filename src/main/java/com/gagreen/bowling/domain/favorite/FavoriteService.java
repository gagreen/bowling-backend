package com.gagreen.bowling.domain.favorite;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterRepository;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final BowlingCenterRepository bowlingCenterRepository;

    public boolean isRegistered(UserVo user, Long centerId) {
        return favoriteRepository.existsByCenterIdAndUserId(centerId, user.getId());
    }

    public void addFavorite(UserVo user, Long centerId) {

        BowlingCenterVo bowlingCenter =  bowlingCenterRepository.findById(centerId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 볼링장입니다."));

        FavoriteVo favorite = FavoriteVo.builder()
                .center(bowlingCenter)
                .user(user)
                .build();

        favoriteRepository.save(favorite);
        return;
    }

    public void deleteFavorite(UserVo user, Long centerId) {
        FavoriteVo item = favoriteRepository.findByCenterIdAndUser(centerId, user)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 즐겨찾기입니다."));

        favoriteRepository.delete(item);

        return;
    }

    public Page<BowlingCenterVo> getFavoritesByUser(UserVo user, Pageable pageable) {
        return bowlingCenterRepository.findUserFavorites(user, pageable);
    }
}
