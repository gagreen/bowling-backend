package com.gagreen.bowling.domain.score_record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "롤 추가 요청")
public class RollAddDto {
    
    @Schema(description = "프레임 번호", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "프레임 번호를 입력해주세요.")
    private Integer frameNumber;
    
    @Schema(description = "롤 번호", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "롤 번호를 입력해주세요.")
    private Integer rollNumber;
    
    @Schema(description = "쓰러뜨린 핀 수", example = "7", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "핀 수를 입력해주세요.")
    private Integer pins;
}
