package com.gagreen.bowling.domain.user.dto;

import com.gagreen.bowling.domain.user.UserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 회원가입 요청")
public class SignUpDto {

    @Schema(description = "사용자 계정", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String account;
    
    @Schema(description = "비밀번호", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    
    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Schema(description = "닉네임", example = "길동이", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;
    
    @Schema(description = "전화번호", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    public UserVo toEntity(String encodedPassword, Date now) {

        return UserVo.builder()
                .account(account)
                .pw(encodedPassword)
                .name(name)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .createdAt(now.toInstant())
                .updatedAt(now.toInstant())
                .build();
    }
}