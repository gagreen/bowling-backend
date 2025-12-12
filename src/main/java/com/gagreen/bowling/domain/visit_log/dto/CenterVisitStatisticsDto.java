package com.gagreen.bowling.domain.visit_log.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CenterVisitStatisticsDto {
    private Long centerId;
    private String centerName;
    private Long visitCount;
    private Instant lastVisitedAt;
}

