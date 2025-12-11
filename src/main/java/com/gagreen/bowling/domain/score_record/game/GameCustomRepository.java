package com.gagreen.bowling.domain.score_record.game;

import com.gagreen.bowling.domain.score_record.dto.GameSearchDto;
import com.gagreen.bowling.domain.user.UserVo;

import java.util.List;
import java.util.Optional;

public interface GameCustomRepository {
    List<GameVo> search(GameSearchDto dto, UserVo user);
    
    Optional<GameVo> findByIdWithUserAndCenter(Long gameId, UserVo user);
}
