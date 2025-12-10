package com.gagreen.bowling.domain.waiting;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.user.UserVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitingQueueRepository extends JpaRepository<WaitingQueueVo, Long>, JpaSpecificationExecutor<WaitingQueueVo>, WaitingQueueCustomRepository {

}
