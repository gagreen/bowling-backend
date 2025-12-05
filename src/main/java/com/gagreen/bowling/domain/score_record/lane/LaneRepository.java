package com.gagreen.bowling.domain.score_record.lane;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LaneRepository extends JpaRepository<LaneVo, Long>, JpaSpecificationExecutor<LaneVo> {

}
