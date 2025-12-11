package com.gagreen.bowling.domain.score_record.dto;

import com.gagreen.bowling.domain.score_record.frame.FrameVo;
import com.gagreen.bowling.domain.score_record.roll.RollVo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RollResponseDto {
    private Long id;
    private Long frameId;
    private Integer frameNumber;
    private Integer rollNumber;
    private Integer pins;

    public static RollResponseDto from(RollVo roll, FrameVo frame) {
        RollResponseDto dto = new RollResponseDto();
        dto.setId(roll.getId());
        dto.setFrameId(frame.getId());
        dto.setFrameNumber(frame.getFrameNumber());
        dto.setRollNumber(roll.getRollNumber());
        dto.setPins(roll.getPins());
        return dto;
    }
}
