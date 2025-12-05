package com.gagreen.bowling.domain.score_record.frame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface FrameRepository extends JpaRepository<FrameVo, Long>, JpaSpecificationExecutor<FrameVo> {

}
