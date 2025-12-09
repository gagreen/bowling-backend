package com.gagreen.bowling.domain.note.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteWriteDto {
    private Long centerId;
    @NotBlank(message = "내용를 입력해주세요.")
    private String content;
}
