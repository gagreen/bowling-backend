package com.gagreen.bowling.domain.user;

import com.gagreen.bowling.common.SignInResultDto;
import com.gagreen.bowling.common.dto.RefreshTokenRequest;
import com.gagreen.bowling.domain.user.dto.SignInDto;
import com.gagreen.bowling.domain.user.dto.SignUpDto;
import com.gagreen.bowling.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api/auth")
@Tag(name = "사용자 인증", description = "일반 사용자 인증 API")
public class UserAuthController {
    private final UserService userService;

    @Operation(summary = "사용자 로그인", description = "계정과 비밀번호를 통해 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = SignInResultDto.class)))
    @PostMapping("/sign-in")
    public SignInResultDto signIn(@RequestBody SignInDto dto) {
        log.info("로그인 요청 수신 - account: {}", dto.getAccount());
        SignInResultDto signInResultDto = userService.signIn(dto.getAccount(), dto.getPassword());
        log.info("로그인 응답 반환 - account: {}", dto.getAccount());
        return signInResultDto;
    }

    @Operation(summary = "인증 테스트", description = "JWT 토큰을 통한 인증 테스트용 엔드포인트입니다.")
    @ApiResponse(responseCode = "200", description = "인증 성공")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/test")
    public String test(@AuthenticationPrincipal UserVo user) {
        log.debug("인증 테스트 요청 - userId: {}, username: {}", user.getId(), user.getUsername());
        return "success";
    }

    @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = UserDto.class)))
    @PostMapping("/sign-up")
    public UserDto signUp(@RequestBody SignUpDto dto) {
        log.info("회원가입 요청 수신 - account: {}, name: {}", dto.getAccount(), dto.getName());
        UserDto result = userService.signUp(dto);
        log.info("회원가입 응답 반환 - account: {}", result.getAccount());
        return result;
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
            content = @Content(schema = @Schema(implementation = SignInResultDto.class)))
    @PostMapping("/refresh")
    public SignInResultDto refresh(@RequestBody RefreshTokenRequest request) {
        log.debug("토큰 갱신 요청 수신");
        SignInResultDto result = userService.refresh(request.getRefreshToken());
        log.info("토큰 갱신 응답 반환");
        return result;
    }
}
