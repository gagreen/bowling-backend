package com.gagreen.bowling.domain.visit_log;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.user.UserVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long>, JpaSpecificationExecutor<VisitLog>, VisitLogCustomRepository {
    long countByUser(UserVo user);
    
    long countByCenter(BowlingCenterVo center);
    
    List<VisitLog> findByUserOrderByCreatedAtDesc(UserVo user);
    
    List<VisitLog> findByCenterOrderByCreatedAtDesc(BowlingCenterVo center);
    
    boolean existsByUserAndCenterAndCreatedAtBetween(UserVo user, BowlingCenterVo center, Instant start, Instant end);
}

