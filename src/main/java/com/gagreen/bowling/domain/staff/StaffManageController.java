package com.gagreen.bowling.domain.staff;

import com.gagreen.bowling.domain.staff.dto.StaffAssignCenterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/staff-api/staffs")
@Tag(name = "직원 관리", description = "직원 관리 API")
public class StaffManageController {

    private final StaffService staffService;

    @Operation(summary = "볼링장 배정", description = "로그인한 직원에게 볼링장을 배정합니다.")
    @ApiResponse(responseCode = "200", description = "배정 성공",
            content = @Content(schema = @Schema(implementation = StaffVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @PatchMapping("/me/center")
    public StaffVo assignCenter(@RequestBody StaffAssignCenterDto dto) {
        return staffService.assignCenter(dto.getCenterId());
    }
}
