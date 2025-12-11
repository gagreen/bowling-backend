package com.gagreen.bowling.domain.waiting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaitingStatusDto {
    private Integer waitingTeam;
    private boolean didRegister;
    private boolean shouldWait;
}
