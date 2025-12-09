package com.gagreen.bowling.domain.bowling_center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BowlingCenterRepository extends JpaRepository<BowlingCenterVo, Long>, JpaSpecificationExecutor<BowlingCenterVo>, BowlingCenterCustomRepository {

}
