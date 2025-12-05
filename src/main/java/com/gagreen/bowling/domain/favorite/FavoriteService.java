package com.gagreen.bowling.domain.favorite;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterService;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.user.UserService;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final BowlingCenterService bowlingCenterService;
    private final UserService userService;

    public boolean isRegistered(Long centerId, Long userId) {
        return favoriteRepository.existsByCenterIdAndUserId(centerId, userId);
    }

    public void addFavorite(Long centerId, Long userId) {

        BowlingCenterVo bowlingCenter =  bowlingCenterService.getItem(centerId);
        UserVo user = userService.getItem(userId);

        FavoriteCenterVo favorite = FavoriteCenterVo.builder()
                .center(bowlingCenter)
                .user(user)
                .build();

        favoriteRepository.save(favorite);
        return;
    }

    public void deleteFavorite(Long centerId, Long userId) {
        FavoriteCenterVo item = favoriteRepository.findByCenterIdAndUserId(centerId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 즐겨찾기입니다."));

        favoriteRepository.delete(item);

        return;
    }
}
