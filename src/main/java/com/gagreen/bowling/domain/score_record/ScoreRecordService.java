package com.gagreen.bowling.domain.score_record;

import com.gagreen.bowling.domain.score_record.dto.GameSearchDto;
import com.gagreen.bowling.domain.score_record.game.GameRepository;
import com.gagreen.bowling.domain.score_record.game.GameSpecification;
import com.gagreen.bowling.domain.score_record.game.GameVo;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreRecordService {
    public final GameRepository gameRepository;

    public void recordScore() {
        // 점수 기록
    }

    public List<GameVo> getGames(GameSearchDto dto) {
        Specification<GameVo> spec = GameSpecification.search(dto);

        // 점수 조회
        List<GameVo> list = gameRepository.findAll(spec);

        return list;
    }

    public GameVo getGameDetail(Long id) {
        GameVo gameVo = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 게임입니다."));



        // 점수 조회
        return gameVo;
    }
}
