package com.gagreen.bowling.domain.score_record.game;

import com.gagreen.bowling.domain.bowling_center.QBowlingCenterVo;
import com.gagreen.bowling.domain.score_record.dto.GameSearchDto;
import com.gagreen.bowling.domain.user.QUserVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class GameCustomRepositoryImpl implements GameCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<GameVo> search(GameSearchDto dto, UserVo user) {
        QGameVo game = QGameVo.gameVo;
        QUserVo userAlias = QUserVo.userVo;
        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;

        return queryFactory
                .select(game)
                .from(game)
                .leftJoin(game.user, userAlias).fetchJoin()
                .leftJoin(game.center, center).fetchJoin()
                .where(
                        game.user.eq(user),
                        startDateCondition(dto.getStartDate(), game),
                        endDateCondition(dto.getEndDate(), game)
                )
                .orderBy(game.createdAt.desc())
                .fetch();
    }

    @Override
    public Optional<GameVo> findByIdWithUserAndCenter(Long gameId, UserVo user) {
        QGameVo game = QGameVo.gameVo;
        QUserVo userAlias = QUserVo.userVo;
        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;

        GameVo result = queryFactory
                .select(game)
                .from(game)
                .leftJoin(game.user, userAlias).fetchJoin()
                .leftJoin(game.center, center).fetchJoin()
                .where(
                        game.id.eq(gameId),
                        game.user.eq(user)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanExpression startDateCondition(LocalDate startDate, QGameVo game) {
        if (startDate == null) {
            return null;
        }
        Instant start = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return game.createdAt.goe(start);
    }

    private BooleanExpression endDateCondition(LocalDate endDate, QGameVo game) {
        if (endDate == null) {
            return null;
        }
        // endDate의 다음 날 00:00:00 이전 (즉, endDate 23:59:59까지)
        Instant end = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return game.createdAt.lt(end);
    }
}
