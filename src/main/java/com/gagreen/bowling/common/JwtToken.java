package com.gagreen.bowling.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
@Schema(description = "JWT 토큰 응답")
public class JwtToken {
    @Schema(description = "인증 타입", example = "Bearer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String grantType; // JWT에 대한 인증 타입, Bearer 인증 방식 사용할 예정
    
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accessToken;
    
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}