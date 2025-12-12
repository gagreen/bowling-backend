package com.gagreen.bowling.domain.visit_log.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitLogStatisticsDto {
    private Long totalVisits;
    private Long centerId;
    private String centerName;
    private Long userId;
    private String userName;
    
    // 기간별 통계
    private Long todayVisits;
    private Long thisWeekVisits;
    private Long thisMonthVisits;
    
    // 최근 방문 내역
    private List<VisitLogItemDto> recentVisits;
    
    // 볼링장별 방문 통계 (사용자별 통계에 사용)
    private List<CenterVisitStatisticsDto> centerVisitStatistics;
}

