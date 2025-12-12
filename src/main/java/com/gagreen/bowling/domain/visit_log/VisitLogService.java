package com.gagreen.bowling.domain.visit_log;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterRepository;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.staff.StaffRepository;
import com.gagreen.bowling.domain.staff.StaffVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.domain.visit_log.dto.VisitLogItemDto;
import com.gagreen.bowling.domain.visit_log.dto.VisitLogRegisterDto;
import com.gagreen.bowling.domain.visit_log.dto.VisitLogStatisticsDto;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitLogService {
    private final VisitLogRepository visitLogRepository;
    private final BowlingCenterRepository bowlingCenterRepository;
    private final StaffRepository staffRepository;

    /**
     * 방문 등록
     * @param dto 방문 등록 요청 DTO
     * @return 등록된 방문 로그
     */
    @Transactional
    public VisitLog registerVisit(VisitLogRegisterDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        
        BowlingCenterVo center = bowlingCenterRepository.findById(dto.getCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 볼링장입니다."));
        
        VisitLog visitLog = new VisitLog();
        visitLog.setUser(user);
        visitLog.setCenter(center);
        visitLog.setCreatedAt(Instant.now());
        
        return visitLogRepository.save(visitLog);
    }

    /**
     * Staff가 배정된 볼링장의 방문 통계 조회
     * @return 방문 통계 DTO
     */
    @Transactional(readOnly = true)
    public VisitLogStatisticsDto getAssignedCenterStatistics() {
        StaffVo staff = SecurityUtil.getCurrentStaff();
        StaffVo persistedStaff = staffRepository.findById(staff.getId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 스태프입니다."));
        
        if (persistedStaff.getCenter() == null) {
            throw new BadRequestException("센터에 배정되지 않은 스태프입니다.");
        }
        
        BowlingCenterVo center = persistedStaff.getCenter();
        
        // 전체 방문 수
        Long totalVisits = visitLogRepository.countByCenter(center);
        
        // 기간별 통계
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        
        Instant todayStart = startOfDay.atZone(ZoneId.systemDefault()).toInstant();
        Instant weekStart = startOfWeek.atZone(ZoneId.systemDefault()).toInstant();
        Instant monthStart = startOfMonth.atZone(ZoneId.systemDefault()).toInstant();
        Instant tomorrowStart = startOfDay.plusDays(1).atZone(ZoneId.systemDefault()).toInstant();
        
        Long todayVisits = visitLogRepository.countByCenterAndDateRange(center.getId(), todayStart, tomorrowStart);
        Long thisWeekVisits = visitLogRepository.countByCenterAndDateRange(center.getId(), weekStart, now);
        Long thisMonthVisits = visitLogRepository.countByCenterAndDateRange(center.getId(), monthStart, now);
        
        // 최근 방문 내역 (최근 10건)
        List<VisitLog> recentVisits = visitLogRepository.findByCenterOrderByCreatedAtDesc(center)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
        
        List<VisitLogItemDto> recentVisitDtos = recentVisits.stream()
                .map(VisitLogItemDto::from)
                .collect(Collectors.toList());
        
        return VisitLogStatisticsDto.builder()
                .totalVisits(totalVisits)
                .centerId(center.getId())
                .centerName(center.getName())
                .todayVisits(todayVisits)
                .thisWeekVisits(thisWeekVisits)
                .thisMonthVisits(thisMonthVisits)
                .recentVisits(recentVisitDtos)
                .build();
    }
}

