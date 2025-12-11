package com.gagreen.bowling.domain.bowling_center;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/staff-api/centers")
@Tag(name = "볼링장 관리", description = "직원 볼링장 관리 API")
public class BowlingCenterManageController {

    private final BowlingCenterService service;

    @Operation(summary = "배정된 볼링장 조회", description = "현재 로그인한 직원이 배정된 볼링장 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BowlingCenterVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public BowlingCenterVo getAssignedCenter() {
        return service.getAssignedCenter();
    }

    @Operation(summary = "배정된 볼링장 수정", description = "현재 로그인한 직원이 배정된 볼링장 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = BowlingCenterVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/me")
    public BowlingCenterVo updateAssignedCenter(@Valid @RequestBody BowlingCenterUpdateDto dto) {
        return service.updateAssignedCenter(dto);
    }
}
