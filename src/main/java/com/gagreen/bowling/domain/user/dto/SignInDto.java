package com.gagreen.bowling.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "사용자 로그인 요청")
public class SignInDto {
    @Schema(description = "사용자 계정", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String account;
    
    @Schema(description = "비밀번호", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}