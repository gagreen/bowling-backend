package com.gagreen.bowling.domain.user.dto;

import com.gagreen.bowling.domain.user.UserVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {

    private String account;
    private String password;
    private String name;
    private String nickname;
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