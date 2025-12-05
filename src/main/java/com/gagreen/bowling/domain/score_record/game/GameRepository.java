package com.gagreen.bowling.domain.score_record.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameVo, Long>, JpaSpecificationExecutor<GameVo> {

}
