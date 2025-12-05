package com.gagreen.bowling.domain.score_record.game;

import com.gagreen.bowling.domain.score_record.dto.GameSearchDto;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GameSpecification {
    public static Specification<GameVo> search(GameSearchDto dto) {
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // --- 기간 검색 (createdAt 기준) ---
            Path<LocalDate> createdAt = root.get("createdAt");

            // startDate 이상
            if (dto.getStartDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(createdAt, dto.getStartDate())
                );
            }

            // endDate 이하
            if (dto.getEndDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(createdAt, dto.getEndDate())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });

    }
}
