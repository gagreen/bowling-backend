package com.gagreen.bowling.domain.waiting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WaitingQueueRepository extends JpaRepository<WaitingQueueVo, Long>, JpaSpecificationExecutor<WaitingQueueVo> {

}
