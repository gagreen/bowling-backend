package com.gagreen.bowling.domain.score_record.dto;

import com.gagreen.bowling.domain.score_record.game.GameVo;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class GameDetailDto {
    private Long id;
    private Long userId;
    private Long centerId;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer totalScore;
    private Integer strikeCount;
    private Integer spareCount;
    private Integer gutterCount;
    private List<FrameDetailDto> frames;

    public static GameDetailDto from(GameVo game, List<FrameDetailDto> frames) {
        GameDetailDto dto = new GameDetailDto();
        dto.setId(game.getId());
        dto.setUserId(game.getUser() != null ? game.getUser().getId() : null);
        dto.setCenterId(game.getCenter() != null ? game.getCenter().getId() : null);
        dto.setCreatedAt(game.getCreatedAt());
        dto.setUpdatedAt(game.getUpdatedAt());
        dto.setTotalScore(game.getTotalScore());
        dto.setStrikeCount(game.getStrikeCount());
        dto.setSpareCount(game.getSpareCount());
        dto.setGutterCount(game.getGutterCount());
        dto.setFrames(frames);
        return dto;
    }
}
