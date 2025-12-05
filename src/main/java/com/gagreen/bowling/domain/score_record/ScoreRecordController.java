package com.gagreen.bowling.domain.score_record;

import com.gagreen.bowling.domain.score_record.dto.GameSearchDto;
import com.gagreen.bowling.domain.score_record.game.GameVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScoreRecordController {
    public final ScoreRecordService scoreRecordService;


    public void recordScore() {
        // 점수 기록
        scoreRecordService.recordScore();
    }

    @GetMapping
    public List<GameVo> getGames(@ModelAttribute GameSearchDto dto) {
        // 점수 조회
        return scoreRecordService.getGames(dto);
    }

}
