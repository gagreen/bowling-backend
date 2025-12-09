package com.gagreen.bowling.domain.note;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterCustomRepository;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterCustomRepositoryImpl;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.bowling_center.QBowlingCenterVo;
import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.favorite.QFavoriteVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class NoteCustomRepositoryImpl implements NoteCustomRepository {
    private final JPAQueryFactory queryFactory;

    public Page<CenterNoteVo> findUserNotes(UserVo user, BowlingCenterSearchDto dto) {
        Pageable pageable = dto.toPageable();

        QCenterNoteVo noteVo = QCenterNoteVo.centerNoteVo;
        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;

        BooleanBuilder builder = new BooleanBuilder();

        // 1) keyword 검색
        builder.and(BowlingCenterCustomRepositoryImpl.keywordSearch(dto, center));

        // 2) 상세 검색 분리
        builder.and(BowlingCenterCustomRepositoryImpl.detailSearch(dto, center));

        List<CenterNoteVo> list = queryFactory
                .select(noteVo)
                .from(noteVo)
                .join(noteVo.center, center).fetchJoin()
                .where(
                        noteVo.user.eq(user),
                        builder
                )
                .offset(pageable.getOffset())   // 페이지 번호
                .limit(pageable.getPageSize())  // 페이지 사이즈
                .fetch();

        Long count = queryFactory		// 총 개수 조회
                .select(center.count())
                .from(noteVo)
                .join(noteVo.center, center)
                .where(
                        noteVo.user.eq(user),
                        builder
                )
                .fetchOne();

        return new PageImpl<>(list, pageable, count);
    }

}
