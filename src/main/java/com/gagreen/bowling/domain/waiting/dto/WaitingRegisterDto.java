package com.gagreen.bowling.domain.waiting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "대기 등록 요청")
public class WaitingRegisterDto {
    private Long centerId;
    @Schema(description = "인원 수", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "인원 수를 입력해 주세요.")
    @Positive(message = "인원 수는 1명 이상이어야 합니다.")
    private Integer peopleCount;
}
