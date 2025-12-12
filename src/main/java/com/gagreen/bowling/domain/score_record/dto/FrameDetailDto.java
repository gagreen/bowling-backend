package com.gagreen.bowling.domain.score_record.dto;

import com.gagreen.bowling.domain.score_record.frame.FrameVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class FrameDetailDto {
    private Long id;
    private Integer frameNumber;
    private Integer frameScore;
    private Boolean isCompleted;
    private List<RollDetailDto> rolls;

    public static FrameDetailDto from(FrameVo frame, List<RollDetailDto> rolls) {
        FrameDetailDto dto = new FrameDetailDto();
        dto.setId(frame.getId());
        dto.setFrameNumber(frame.getFrameNumber());
        dto.setFrameScore(frame.getFrameScore());
        dto.setIsCompleted(frame.getIsCompleted());
        dto.setRolls(rolls);
        return dto;
    }
}
