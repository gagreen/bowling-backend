package com.gagreen.bowling.domain.bowling_center;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.favorite.QFavoriteVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class BowlingCenterCustomRepositoryImpl implements BowlingCenterCustomRepository {
    private final JPAQueryFactory queryFactory;

    public Page<BowlingCenterVo> findUserFavorites(UserVo user, Pageable pageable) {

        QFavoriteVo favorite = QFavoriteVo.favoriteVo;
        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;

        List<BowlingCenterVo> list = queryFactory
                .select(center)
                .from(favorite)
                .join(favorite.center, center)
                .where(
                        favorite.user.eq(user)
                )
                .offset(pageable.getOffset())   // 페이지 번호
                .limit(pageable.getPageSize())  // 페이지 사이즈
                .fetch();

        Long count = queryFactory		// 총 개수 조회
                .select(center.count())
                .from(favorite)
                .innerJoin(favorite.center, center)
                .where(
                        favorite.user.eq(user)
                )
                .fetchOne();

        return new PageImpl<>(list, pageable, count);
    }

    public Page<BowlingCenterVo> search(BowlingCenterSearchDto dto) {
        Pageable pageable = dto.toPageable();

        QBowlingCenterVo center = QBowlingCenterVo.bowlingCenterVo;

        BooleanBuilder builder = new BooleanBuilder();

        // 1) keyword 검색
        builder.and(keywordSearch(dto, center));

        // 2) 상세 검색 분리
        builder.and(detailSearch(dto, center));

        List<BowlingCenterVo> list = queryFactory
                .select(center)
                .from(center)
                .where(
                        builder
                )
                .offset(pageable.getOffset())   // 페이지 번호
                .limit(pageable.getPageSize())  // 페이지 사이즈
                .fetch();

        Long count = queryFactory		// 총 개수 조회
                .select(center.count())
                .from(center)
                .where(
                        builder
                )
                .fetchOne();

        return new PageImpl<>(list, pageable, count);
    }

    // -------------------------------
    // 1. keyword 검색 분리
    // -------------------------------
    public static BooleanExpression keywordSearch(BowlingCenterSearchDto dto, QBowlingCenterVo center) {

        String keyword = dto.getKeyword();
        String type = dto.getType();

        if (keyword == null || keyword.isBlank()) {
            return null; // cb.conjunction()과 동일, 조건 없음
        }

        keyword = keyword.toLowerCase();

        if (type == null) {
            return Expressions.anyOf(
                    center.name.lower().like("%" + keyword + "%"),
                    center.state.lower().like("%" + keyword + "%"),
                    center.city.lower().like("%" + keyword + "%"),
                    center.district.lower().like("%" + keyword + "%")
            );
        }

        return switch (type) {
            case "name"     -> center.name.lower().like("%" + keyword + "%");
            case "state"    -> center.state.lower().like("%" + keyword + "%");
            case "city"     -> center.city.lower().like("%" + keyword + "%");
            case "district" -> center.district.lower().like("%" + keyword + "%");
            default -> null;
        };
    }

    // -------------------------------
    // 2. 상세 검색(detailSearch) 분리
    // -------------------------------
    public static BooleanBuilder detailSearch(BowlingCenterSearchDto dto, QBowlingCenterVo center) {

        BooleanBuilder builder = new BooleanBuilder();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            builder.and(center.name.lower().like("%" + dto.getName().toLowerCase() + "%"));
        }

        if (dto.getState() != null && !dto.getState().isBlank()) {
            builder.and(center.state.lower().like("%" + dto.getState().toLowerCase() + "%"));
        }

        if (dto.getCity() != null && !dto.getCity().isBlank()) {
            builder.and(center.city.lower().like("%" + dto.getCity().toLowerCase() + "%"));
        }

        if (dto.getDistrict() != null && !dto.getDistrict().isBlank()) {
            builder.and(center.district.lower().like("%" + dto.getDistrict().toLowerCase() + "%"));
        }

        return builder;
    }

}
