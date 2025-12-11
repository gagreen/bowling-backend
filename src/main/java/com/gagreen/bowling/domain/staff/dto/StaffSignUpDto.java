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
@Schema(description = "직원 회원가입 요청")
public class StaffSignUpDto {
    @Schema(description = "볼링장 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long centerId;
    
    @Schema(description = "직원 계정", example = "staff123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String account;
    
    @Schema(description = "비밀번호", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    
    @Schema(description = "이름", example = "김직원", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Schema(description = "전화번호", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;
}
