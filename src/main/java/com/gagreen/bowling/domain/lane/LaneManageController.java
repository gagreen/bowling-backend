package com.gagreen.bowling.domain.lane;

import com.gagreen.bowling.domain.lane.dto.LaneStatusUpdateDto;
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
@RequestMapping("/staff-api/lanes")
@Tag(name = "레인 관리", description = "볼링장 레인 관리 API")
public class LaneManageController {

    private final LaneService laneService;

    @Operation(summary = "내 볼링장 레인 목록 조회", description = "로그인한 직원이 소속된 볼링장의 레인 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = LaneVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping
    public List<LaneVo> getLanesOfMyCenter() {
        log.debug("내 볼링장 레인 목록 조회 요청");
        List<LaneVo> result = laneService.getMyCenterLanes();
        log.debug("내 볼링장 레인 목록 조회 완료 - 레인 수: {}", result.size());
        return result;
    }

    @Operation(summary = "레인 상태 변경", description = "특정 레인의 상태를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "변경 성공",
            content = @Content(schema = @Schema(implementation = LaneVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @PatchMapping("/{laneId}/status")
    public LaneVo updateStatus(@PathVariable Long laneId, @Valid @RequestBody LaneStatusUpdateDto dto) {
        log.info("레인 상태 변경 요청 - laneId: {}, status: {}", laneId, dto.getStatus());
        dto.setLaneId(laneId);
        LaneVo result = laneService.updateLaneStatus(dto);
        log.info("레인 상태 변경 완료 - laneId: {}, status: {}", laneId, result.getStatus());
        return result;
    }
}
