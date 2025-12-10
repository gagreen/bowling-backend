package com.gagreen.bowling.domain.waiting;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.bowling_center.QBowlingCenterVo;
import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.favorite.QFavoriteVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.domain.waiting.dto.WaitingListItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class WaitingQueueCustomRepositoryImpl implements WaitingQueueCustomRepository {
    private final JPAQueryFactory queryFactory;

    public List<WaitingListItem> findByUser(UserVo user) {

        QWaitingQueueVo queue = QWaitingQueueVo.waitingQueueVo;
        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;

        return queryFactory
                .select(Projections.constructor(
                        WaitingListItem.class,
                        queue.id, queue.center.id, center.name, queue.user.id, queue.peopleCount, queue.status, queue.createdAt))
                .from(queue)
                .join(queue.center, center)
                .where(
                        queue.user.eq(user)
                )
                .fetch();
    }

    @Override
    public List<WaitingListItem> findByCenter(BowlingCenterVo centerVo) {
        QWaitingQueueVo queue = QWaitingQueueVo.waitingQueueVo;
        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;

        return queryFactory
                .select(Projections.constructor(
                        WaitingListItem.class,
                        queue.id, queue.center.id, center.name, queue.user.id, queue.peopleCount, queue.status, queue.createdAt))
                .from(queue)
                .join(queue.center, center)
                .where(
                        queue.center.eq(centerVo)
                )
                .fetch();
    }
}
