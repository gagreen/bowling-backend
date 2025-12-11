package com.gagreen.bowling.domain.lane;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.waiting.QLaneAssignmentVo;
import com.gagreen.bowling.domain.lane.code.LaneStatus;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LaneCustomRepositoryImpl implements LaneCustomRepository {
    private final JPAQueryFactory queryFactory;

    //
    @Override
    public List<LaneVo> findAvailableLanes(BowlingCenterVo centerVo) {

        QLaneVo lane = QLaneVo.laneVo;
        QLaneAssignmentVo assignment = QLaneAssignmentVo.laneAssignmentVo;

        return queryFactory
                .select(lane)
                .from(lane)
                .where(
                        lane.center.eq(centerVo),
                        lane.status.eq(LaneStatus.NORMAL.getCode()),
                        JPAExpressions
                                .selectOne()
                                .from(assignment)
                                .where(
                                        assignment.lane.eq(lane),
                                        assignment.finishedAt.isNull()
                                )
                                .notExists()
                )
                .fetch();
    }
}
