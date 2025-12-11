package com.gagreen.bowling.domain.score_record.roll;

import com.gagreen.bowling.domain.score_record.frame.FrameVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RollRepository extends JpaRepository<RollVo, Long>, JpaSpecificationExecutor<RollVo> {
    
    List<RollVo> findByFrameOrderByRollNumber(FrameVo frame);
    
    boolean existsByFrameAndRollNumber(FrameVo frame, Integer rollNumber);
}
