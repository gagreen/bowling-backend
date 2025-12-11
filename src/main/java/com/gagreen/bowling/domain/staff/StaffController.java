package com.gagreen.bowling.domain.staff;

import com.gagreen.bowling.domain.user.UserRepository;
import com.gagreen.bowling.domain.user.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/staff-api/staffs")
@Tag(name = "직원", description = "직원 조회 API")
public class StaffController {

    private final UserRepository userRepository;

    @Operation(summary = "직원 목록 조회", description = "전체 직원 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = UserVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping
    public List<UserVo> getUsers() {
        return userRepository.findAll();
    }
}
