package com.gagreen.bowling.domain.lane.dto;

import com.gagreen.bowling.domain.lane.code.LaneStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "레인 생성 요청")
public class LaneCreateDto {
    
    @Schema(description = "레인 번호", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "레인 번호를 입력해 주세요.")
    private Integer laneNumber;
    
    @Schema(description = "레인 상태", example = "NORMAL")
    private LaneStatus status = LaneStatus.NORMAL;
}
