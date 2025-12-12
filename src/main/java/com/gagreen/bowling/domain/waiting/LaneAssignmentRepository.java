package com.gagreen.bowling.domain.waiting;

import com.gagreen.bowling.domain.lane.LaneVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LaneAssignmentRepository extends JpaRepository<LaneAssignmentVo, Long>, JpaSpecificationExecutor<LaneAssignmentVo> {

    boolean existsByLaneAndFinishedAtIsNull(LaneVo lane);
    
    Optional<LaneAssignmentVo> findByLaneAndFinishedAtIsNull(LaneVo lane);
}
