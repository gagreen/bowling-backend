package com.gagreen.bowling.domain.user;

import com.gagreen.bowling.common.JwtToken;
import com.gagreen.bowling.domain.user.dto.SignInDto;
import com.gagreen.bowling.domain.user.dto.SignUpDto;
import com.gagreen.bowling.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api/auth")
public class UserAuthController {
    private final UserService userService;

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody SignInDto dto) {
        String account = dto.getAccount();
        String password = dto.getPassword();

        log.info("request username = {}, password = {}", account, password);

        JwtToken jwtToken = userService.signIn(account, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

    @PostMapping("/test")
    public String test(@AuthenticationPrincipal UserVo user) {
        log.info("username = {}", user.getUsername());

        return "success";
    }

    @PostMapping("/sign-up")
    public UserDto signUp(@RequestBody SignUpDto dto) {
        return userService.signUp(dto);
    }
}
