package com.gagreen.bowling.domain.lane;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LaneRepository extends JpaRepository<LaneVo, Long>, JpaSpecificationExecutor<LaneVo>, LaneCustomRepository {

    List<LaneVo> findByCenter(BowlingCenterVo center);
    
    long countByCenter(BowlingCenterVo center);
}
