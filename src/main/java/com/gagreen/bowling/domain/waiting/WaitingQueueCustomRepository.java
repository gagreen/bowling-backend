package com.gagreen.bowling.domain.waiting;


import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.domain.waiting.dto.WaitingListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WaitingQueueCustomRepository {
    List<WaitingListItem> findByUser(UserVo user);

    List<WaitingListItem> findByCenter(BowlingCenterVo user);

    Optional<WaitingListItem> findByUserAndCenter(UserVo user, BowlingCenterVo centerVo);

    Optional<Long> countByCenterAndStatus(BowlingCenterVo center, String status);
}
