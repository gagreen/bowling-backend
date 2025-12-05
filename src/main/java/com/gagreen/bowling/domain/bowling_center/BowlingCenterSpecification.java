package com.gagreen.bowling.domain.bowling_center;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BowlingCenterSpecification {
    public static Specification<BowlingCenterVo> search(BowlingCenterSearchDto dto) {


        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dto.getKeyword() == null || dto.getKeyword().isBlank()) {
                predicates.add(cb.conjunction());
            } else if (dto.getType() == null) {
                List<Predicate> keywordPredicates = new ArrayList<>();

                keywordPredicates.add(cb.like(cb.lower(root.get("name")), "%" + dto.getKeyword().toLowerCase() + "%"));
                keywordPredicates.add(cb.like(cb.lower(root.get("state")), "%" + dto.getKeyword().toLowerCase() + "%"));
                keywordPredicates.add(cb.like(cb.lower(root.get("city")), "%" + dto.getKeyword().toLowerCase() + "%"));
                keywordPredicates.add(cb.like(cb.lower(root.get("district")), "%" + dto.getKeyword().toLowerCase() + "%"));

                predicates.add(cb.or(keywordPredicates.toArray(new Predicate[0])));

            } else if (dto.getType().equals("name")) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + dto.getKeyword().toLowerCase() + "%"));

            } else if (dto.getType().equals("state")) {
                predicates.add(cb.like(cb.lower(root.get("state")), "%" + dto.getKeyword().toLowerCase() + "%"));

            } else if (dto.getType().equals("city")) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + dto.getKeyword().toLowerCase() + "%"));

            } else if (dto.getType().equals("district")) {
                predicates.add(cb.like(cb.lower(root.get("district")), "%" + dto.getKeyword().toLowerCase() + "%"));

            }

            predicates.add(detailSearch(dto).toPredicate(root, query, cb));

            return cb.and(predicates.toArray(new Predicate[0]));
        });

    }

    private static Specification<BowlingCenterVo> detailSearch(BowlingCenterSearchDto dto) {


        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (dto.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + dto.getName().toLowerCase() + "%"));
            }

            if (dto.getState() != null) {
                predicates.add(cb.like(cb.lower(root.get("state")), "%" + dto.getState().toLowerCase() + "%"));
            }

            if (dto.getCity() != null) {
                predicates.add(cb.like(cb.lower(root.get("state")), "%" + dto.getCity().toLowerCase() + "%"));
            }

            if (dto.getDistrict() != null) {
                predicates.add(cb.like(cb.lower(root.get("district")), "%" + dto.getDistrict().toLowerCase() + "%"));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }
}
