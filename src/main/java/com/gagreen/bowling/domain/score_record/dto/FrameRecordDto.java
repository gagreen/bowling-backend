package com.gagreen.bowling.domain.score_record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "프레임 기록")
public class FrameRecordDto {
    
    @Schema(description = "프레임 번호", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "프레임 번호를 입력해주세요.")
    private Integer frameNumber;
    
    @Schema(description = "롤 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotNull(message = "롤 목록을 입력해주세요.")
    private List<RollRecordDto> rolls;
}
