package com.gagreen.bowling.domain.bowling_center;


import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.user.UserVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BowlingCenterCustomRepository {
    Page<BowlingCenterVo> findUserFavorites(UserVo user, Pageable pageable);

    Page<BowlingCenterVo> search(BowlingCenterSearchDto dto);

}
