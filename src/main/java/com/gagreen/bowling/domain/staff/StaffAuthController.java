package com.gagreen.bowling.domain.staff;

import com.gagreen.bowling.common.SignInResultDto;
import com.gagreen.bowling.common.dto.RefreshTokenRequest;
import com.gagreen.bowling.domain.user.dto.SignInDto;
import com.gagreen.bowling.domain.staff.dto.StaffSignUpDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/staff-api/auth")
@Tag(name = "직원 인증", description = "볼링장 직원 인증 API")
public class StaffAuthController {

    private final StaffService staffService;

    @Operation(summary = "직원 로그인", description = "직원 계정과 비밀번호를 통해 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = SignInResultDto.class)))
    @PostMapping("/sign-in")
    public SignInResultDto signIn(@RequestBody SignInDto dto) {
        return staffService.signIn(dto);
    }

    @Operation(summary = "직원 회원가입", description = "새로운 직원을 등록합니다. 볼링장에 소속되어야 합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = StaffVo.class)))
    @PostMapping("/sign-up")
    public StaffVo signUp(@RequestBody StaffSignUpDto dto) {
        return staffService.signUp(dto);
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
            content = @Content(schema = @Schema(implementation = SignInResultDto.class)))
    @PostMapping("/refresh")
    public SignInResultDto refresh(@RequestBody RefreshTokenRequest request) {
        return staffService.refresh(request.getRefreshToken());
    }
}
