package com.gagreen.bowling.domain.score_record.frame;

import com.gagreen.bowling.domain.score_record.game.GameVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FrameRepository extends JpaRepository<FrameVo, Long>, JpaSpecificationExecutor<FrameVo> {
    
    Optional<FrameVo> findByGameAndFrameNumber(GameVo game, Integer frameNumber);
    
    List<FrameVo> findByGameOrderByFrameNumber(GameVo game);
    
    boolean existsByGameAndFrameNumber(GameVo game, Integer frameNumber);
}
