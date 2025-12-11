package com.gagreen.bowling.domain.score_record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게임 시작 요청")
public class GameStartDto {
    
    @Schema(description = "볼링장 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "볼링장 ID를 입력해주세요.")
    private Long centerId;
}
