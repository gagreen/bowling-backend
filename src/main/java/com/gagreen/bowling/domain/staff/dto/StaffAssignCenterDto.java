package com.gagreen.bowling.domain.staff.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "볼링장 배정 요청")
public class StaffAssignCenterDto {
    @Schema(description = "볼링장 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long centerId;
}
