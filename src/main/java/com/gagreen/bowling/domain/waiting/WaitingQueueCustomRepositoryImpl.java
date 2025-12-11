package com.gagreen.bowling.domain.waiting;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.bowling_center.QBowlingCenterVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.domain.waiting.dto.WaitingListItem;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class WaitingQueueCustomRepositoryImpl implements WaitingQueueCustomRepository {
    private final JPAQueryFactory queryFactory;

    public List<WaitingListItem> findByUser(UserVo user) {

        QWaitingQueueVo queue = QWaitingQueueVo.waitingQueueVo;
        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;

        return queryFactory
                .select(Projections.constructor(
                        WaitingListItem.class,
                        queue.id,
                        queue.center.id,
                        center.name,
                        queue.user.id,
                        queue.peopleCount,
                        queue.status,
                        queue.createdAt,
                        queue.orderNo
                ))
                .from(queue)
                .join(queue.center, center)
                .where(
                        queue.user.eq(user)
                )
                .orderBy(queue.orderNo.asc())
                .fetch();
    }

    @Override
    public List<WaitingListItem> findByCenter(BowlingCenterVo centerVo) {
        QWaitingQueueVo queue = QWaitingQueueVo.waitingQueueVo;
        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;

        return queryFactory
                .select(Projections.constructor(
                        WaitingListItem.class,
                        queue.id,
                        queue.center.id,
                        center.name,
                        queue.user.id,
                        queue.peopleCount,
                        queue.status,
                        queue.createdAt,
                        queue.orderNo
                ))
                .from(queue)
                .join(queue.center, center)
                .where(
                        queue.center.eq(centerVo)
                )
                .orderBy(queue.orderNo.asc())
                .fetch();
    }

    public Optional<WaitingListItem> findByUserAndCenter(UserVo user, BowlingCenterVo centerVo) {

        QWaitingQueueVo queue = QWaitingQueueVo.waitingQueueVo;
        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;

        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(
                        WaitingListItem.class,
                        queue.id,
                        queue.center.id,
                        center.name,
                        queue.user.id,
                        queue.peopleCount,
                        queue.status,
                        queue.createdAt,
                        queue.orderNo
                ))
                .from(queue)
                .join(queue.center, center)
                .where(
                        queue.user.eq(user),
                        queue.center.eq(centerVo)
                )
                .fetchOne());
    }

    @Override
    public Optional<Long> countByCenterAndStatus(BowlingCenterVo center, String status) {
        QWaitingQueueVo queue = QWaitingQueueVo.waitingQueueVo;

        return Optional.ofNullable(queryFactory
                .select(queue.count())
                .from(queue)
                .where(
                        queue.center.eq(center),
                        queue.status.eq(status)
                )
                .fetchOne());
    }
}
