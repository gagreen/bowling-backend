package com.gagreen.bowling.domain.favorite;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/centers/{centerId}/favorites")
    public void add(@PathVariable Long centerId) {
        UserVo user = SecurityUtil.getCurrentUser();

        if (favoriteService.isRegistered(user, centerId)) {
            throw new BadRequestException("이미 즐겨찾기에 추가된 볼링장입니다.");
        }
        // 즐겨찾기 추가
        favoriteService.addFavorite(user, centerId);

        return;
    }

    @DeleteMapping("/centers/{centerId}/favorites")
    public void delete(@PathVariable Long centerId) {
        UserVo user = SecurityUtil.getCurrentUser();

        if (!favoriteService.isRegistered(user, centerId)) {
            throw new BadRequestException("즐겨찾기에 등록되지 않은 볼링장입니다.");
        }

        // 즐겨찾기 제거
        favoriteService.deleteFavorite(user, centerId);
    }

    @GetMapping("/favorites")
    public Page<BowlingCenterVo> getFavorites(Pageable dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        // 즐겨찾기한 볼링장 목록 조회
        Page<BowlingCenterVo> favorites = favoriteService.getFavoritesByUser(user, dto);

        return favorites;
    }


}
