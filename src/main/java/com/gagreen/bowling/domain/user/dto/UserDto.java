package com.gagreen.bowling.domain.user.dto;

import com.gagreen.bowling.domain.user.UserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 정보 응답")
public class UserDto {

    @Schema(description = "사용자 계정", example = "user123")
    private String account;
    
    @Schema(description = "이름", example = "홍길동")
    private String name;
    
    @Schema(description = "닉네임", example = "길동이")
    private String nickname;
    
    @Schema(description = "전화번호", example = "01012345678")
    private String phoneNumber;

    static public UserDto toDto(UserVo user) {
        return UserDto.builder()
                .account(user.getAccount())
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
