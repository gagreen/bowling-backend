package com.gagreen.bowling.domain.score_record.dto;

import com.gagreen.bowling.domain.score_record.roll.RollVo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RollDetailDto {
    private Long id;
    private Integer rollNumber;
    private Integer pins;

    public static RollDetailDto from(RollVo roll) {
        RollDetailDto dto = new RollDetailDto();
        dto.setId(roll.getId());
        dto.setRollNumber(roll.getRollNumber());
        dto.setPins(roll.getPins());
        return dto;
    }
}
