package com.gagreen.bowling.domain.waiting;

import com.gagreen.bowling.domain.waiting.dto.LaneAssignmentDto;
import com.gagreen.bowling.domain.waiting.dto.WaitingListItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/staff-api/waiting")
@Tag(name = "대기열 (직원)", description = "직원 대기열 관리 API")
public class WaitingStaffController {

    private final WaitingService waitingService;

    @Operation(summary = "대기열 조회", description = "배정된 센터의 대기열을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = WaitingListItem.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/queues")
    public List<WaitingListItem> getCenterQueues() {
        log.debug("센터 대기열 조회 요청");
        List<WaitingListItem> result = waitingService.getMyCenterQueues();
        log.debug("센터 대기열 조회 완료 - 대기 건수: {}", result.size());
        return result;
    }

    @Operation(summary = "레인 배정", description = "대기열의 항목에 레인을 배정합니다. 레인 배정 시 자동으로 대기 상태가 DONE으로 변경됩니다.")
    @ApiResponse(responseCode = "200", description = "배정 성공")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/queues/assign")
    public void assignLaneToQueue(@Valid @RequestBody LaneAssignmentDto dto) {
        log.info("레인 배정 요청 - queueId: {}, laneId: {}", dto.getQueueId(), dto.getLaneId());
        waitingService.assignLaneToQueue(dto);
        log.info("레인 배정 완료 - queueId: {}, laneId: {}", dto.getQueueId(), dto.getLaneId());
    }

}
