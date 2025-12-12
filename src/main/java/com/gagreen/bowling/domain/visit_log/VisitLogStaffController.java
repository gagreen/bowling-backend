package com.gagreen.bowling.domain.visit_log;

import com.gagreen.bowling.domain.visit_log.dto.VisitLogStatisticsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/staff-api/visit-logs")
@Tag(name = "방문 통계 (직원)", description = "볼링장 방문 통계 조회 API")
public class VisitLogStaffController {
    
    private final VisitLogService visitLogService;

    @Operation(summary = "배정된 볼링장 방문 통계 조회", description = "현재 로그인한 직원이 배정된 볼링장의 방문 통계를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = VisitLogStatisticsDto.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/statistics")
    public VisitLogStatisticsDto getAssignedCenterStatistics() {
        return visitLogService.getAssignedCenterStatistics();
    }
}

