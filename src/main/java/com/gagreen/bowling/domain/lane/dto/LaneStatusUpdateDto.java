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
@Schema(description = "레인 상태 변경 요청")
public class LaneStatusUpdateDto {
    @Schema(description = "레인 ID", example = "1")
    private Long laneId;
    
    @Schema(description = "변경할 레인 상태", example = "AVAILABLE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "변경할 상태를 입력해 주세요.")
    private LaneStatus status;
}
