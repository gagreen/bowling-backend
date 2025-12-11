package com.gagreen.bowling.domain.score_record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "게임 기록 요청")
public class GameRecordDto {
    
    @Schema(description = "볼링장 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "볼링장 ID를 입력해주세요.")
    private Long centerId;
    
    @Schema(description = "프레임 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotNull(message = "프레임 목록을 입력해주세요.")
    private List<FrameRecordDto> frames;
}
