package com.gagreen.bowling.security;

import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.domain.staff.StaffVo;
import com.gagreen.bowling.exception.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    // 현재 회원 정보 조회
    public static UserVo getCurrentUser() throws AuthenticationCredentialsNotFoundException {
        // 현재 실행 중인 스레드에 저장했던 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw new AuthenticationCredentialsNotFoundException();
        }
        return (UserVo) authentication.getPrincipal();
    }

    // 현재 스태프 정보 조회
    public static StaffVo getCurrentStaff() throws AuthenticationCredentialsNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof StaffVo)) {
            throw new AuthenticationCredentialsNotFoundException();
        }

        return (StaffVo) authentication.getPrincipal();
    }
}