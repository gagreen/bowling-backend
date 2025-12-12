package com.gagreen.bowling.domain.visit_log;

import com.gagreen.bowling.domain.bowling_center.QBowlingCenterVo;
import com.gagreen.bowling.domain.visit_log.dto.CenterVisitStatisticsDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class VisitLogCustomRepositoryImpl implements VisitLogCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Long countByUserAndDateRange(Long userId, Instant startDate, Instant endDate) {
        QVisitLog visitLog = QVisitLog.visitLog;
        
        Long count = queryFactory
                .select(visitLog.count())
                .from(visitLog)
                .where(
                        visitLog.user.id.eq(userId)
                                .and(visitLog.createdAt.goe(startDate))
                                .and(visitLog.createdAt.lt(endDate))
                )
                .fetchOne();
        
        return count != null ? count : 0L;
    }

    @Override
    public Long countByCenterAndDateRange(Long centerId, Instant startDate, Instant endDate) {
        QVisitLog visitLog = QVisitLog.visitLog;
        
        Long count = queryFactory
                .select(visitLog.count())
                .from(visitLog)
                .where(
                        visitLog.center.id.eq(centerId)
                                .and(visitLog.createdAt.goe(startDate))
                                .and(visitLog.createdAt.lt(endDate))
                )
                .fetchOne();
        
        return count != null ? count : 0L;
    }

    @Override
    public List<CenterVisitStatisticsDto> getCenterVisitStatisticsByUser(Long userId) {
        QVisitLog visitLog = QVisitLog.visitLog;
        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;
        
        List<Tuple> results = queryFactory
                .select(
                        visitLog.center.id,
                        center.name,
                        visitLog.count(),
                        visitLog.createdAt.max()
                )
                .from(visitLog)
                .join(visitLog.center, center)
                .where(visitLog.user.id.eq(userId))
                .groupBy(visitLog.center.id, center.name)
                .orderBy(visitLog.createdAt.max().desc())
                .fetch();
        
        return results.stream()
                .map(tuple -> CenterVisitStatisticsDto.builder()
                        .centerId(tuple.get(visitLog.center.id))
                        .centerName(tuple.get(center.name))
                        .visitCount(tuple.get(visitLog.count()))
                        .lastVisitedAt(tuple.get(visitLog.createdAt.max()))
                        .build())
                .collect(Collectors.toList());
    }
}

