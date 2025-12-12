package com.gagreen.bowling.domain.visit_log.dto;

import com.gagreen.bowling.domain.visit_log.VisitLog;
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
public class VisitLogItemDto {
    private Long logId;
    private Long centerId;
    private String centerName;
    private Instant visitedAt;
    
    public static VisitLogItemDto from(VisitLog visitLog) {
        return VisitLogItemDto.builder()
                .logId(visitLog.getId())
                .centerId(visitLog.getCenter().getId())
                .centerName(visitLog.getCenter().getName())
                .visitedAt(visitLog.getCreatedAt())
                .build();
    }
}

