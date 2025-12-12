package com.gagreen.bowling.domain.visit_log.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitLogRegisterDto {
    @NotNull(message = "볼링장 ID는 필수입니다.")
    private Long centerId;
}

