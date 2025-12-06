package com.gagreen.bowling.domain.user.dto;

import com.gagreen.bowling.domain.user.UserVo;
import lombok.*;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String account;
    private String name;
    private String nickname;
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
