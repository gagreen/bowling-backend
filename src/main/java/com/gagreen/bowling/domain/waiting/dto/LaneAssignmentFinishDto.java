package com.gagreen.bowling.domain.waiting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "레인 배정 종료 요청")
public class LaneAssignmentFinishDto {
    
    @Schema(description = "레인 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "레인 ID를 입력해 주세요.")
    private Long laneId;
}
