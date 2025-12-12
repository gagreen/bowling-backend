package com.gagreen.bowling.domain.visit_log;

import com.gagreen.bowling.domain.visit_log.dto.VisitLogRegisterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api/visit-logs")
@Tag(name = "방문 기록", description = "방문 등록 API")
public class VisitLogController {
    
    private final VisitLogService visitLogService;

    @Operation(summary = "방문 등록", description = "볼링장 방문을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping
    public void registerVisit(@Valid @RequestBody VisitLogRegisterDto dto) {
        visitLogService.registerVisit(dto);
    }
}

