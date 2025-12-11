package com.gagreen.bowling.domain.waiting;

import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.domain.waiting.dto.WaitingListItem;
import com.gagreen.bowling.domain.waiting.dto.WaitingRegisterDto;
import com.gagreen.bowling.domain.waiting.dto.WaitingStatusDto;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.security.SecurityUtil;
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
@RequestMapping("/user-api")
@Tag(name = "대기열 (사용자)", description = "사용자 대기열 관리 API")
public class WaitingUserController {

    private final WaitingService waitingService;

    @Operation(summary = "볼링장 대기 상태 조회", description = "볼링장에서 바로 입장 가능한지, 대기 등록이 필요한지 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = WaitingStatusDto.class)))
    @GetMapping("/centers/{centerId}/queues/status")
    public WaitingStatusDto getCenterQueueStatus(@PathVariable Long centerId) {
        log.debug("대기 상태 조회 요청 - centerId: {}", centerId);
        Integer waitingTeam = waitingService.getCenterQueues(centerId).size();
        boolean didRegister = waitingService.getMyQueueStatus(centerId);
        boolean shouldWait = waitingService.isShouldWait(centerId);
        log.debug("대기 상태 조회 완료 - centerId: {}, 대기 팀 수: {}, 등록 여부: {}, 대기 필요: {}", 
                centerId, waitingTeam, didRegister, shouldWait);
        return new WaitingStatusDto(waitingTeam, didRegister, shouldWait);
    }


    @Operation(summary = "내 대기 상태 조회", description = "로그인한 사용자의 대기 순번을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = WaitingListItem.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/queues/my")
    public List<WaitingListItem> getMyQueueStatus() {
        log.debug("내 대기 상태 조회 요청");
        List<WaitingListItem> result = waitingService.getMyQueueStatus();
        log.debug("내 대기 상태 조회 완료 - 대기 건수: {}", result.size());
        return result;
    }

    @Operation(summary = "대기 등록", description = "볼링장 대기열에 등록합니다. 레인이 널널하더라도 대기열에 등록한 후 할당됩니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/centers/{centerId}/queues")
    public WaitingQueueVo registerToQueue(
            @PathVariable Long centerId,
            @Valid @RequestBody WaitingRegisterDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        dto.setCenterId(centerId);

        log.info("대기 등록 요청 - userId: {}, centerId: {}, peopleCount: {}", 
                user.getId(), centerId, dto.getPeopleCount());

        boolean didRegister = waitingService.getMyQueueStatus(centerId);

        if (didRegister) {
            log.warn("대기 등록 실패 - 이미 등록됨. userId: {}, centerId: {}", user.getId(), centerId);
            throw new BadRequestException("이미 대기 등록이 되어 있습니다.");
        }

        // 대기 등록 처리 (레인이 널널하더라도 대기열에 등록)
        return waitingService.registerWaiting(user, dto);
    }


}
