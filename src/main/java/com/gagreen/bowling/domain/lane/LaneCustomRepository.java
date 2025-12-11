package com.gagreen.bowling.domain.lane;


import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;

import java.util.List;

public interface LaneCustomRepository {
    List<LaneVo> findAvailableLanes (BowlingCenterVo center);

}
