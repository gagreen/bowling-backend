package com.gagreen.bowling.domain.visit_log;

import com.gagreen.bowling.domain.visit_log.dto.CenterVisitStatisticsDto;

import java.time.Instant;
import java.util.List;

public interface VisitLogCustomRepository {
    Long countByUserAndDateRange(Long userId, Instant startDate, Instant endDate);
    
    Long countByCenterAndDateRange(Long centerId, Instant startDate, Instant endDate);
    
    List<CenterVisitStatisticsDto> getCenterVisitStatisticsByUser(Long userId);
}

