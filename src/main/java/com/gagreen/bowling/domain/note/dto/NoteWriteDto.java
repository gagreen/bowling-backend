package com.gagreen.bowling.domain.note.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "노트 작성 요청")
public class NoteWriteDto {
    @Schema(description = "볼링장 ID", example = "1")
    private Long centerId;
    
    @Schema(description = "노트 내용", example = "볼링장이 깨끗하고 좋았습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "내용를 입력해주세요.")
    private String content;
}
