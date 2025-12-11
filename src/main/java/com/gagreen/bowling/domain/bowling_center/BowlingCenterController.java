package com.gagreen.bowling.domain.bowling_center;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api/centers")
@Tag(name = "볼링장", description = "볼링장 조회 API")
public class BowlingCenterController {

    private final BowlingCenterService service;

    @Operation(summary = "볼링장 목록 조회", description = "검색 조건에 따라 볼링장 목록을 페이지네이션으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BowlingCenterVo.class)))
    @GetMapping
    public Page<BowlingCenterVo> searchList(BowlingCenterSearchDto dto) {

        return service.searchList(dto);
    }

    @Operation(summary = "볼링장 상세 조회", description = "볼링장 ID로 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BowlingCenterVo.class)))
    @GetMapping("/{centerId}")
    public BowlingCenterVo detail(@PathVariable String centerId) {
        if (!StringUtils.isNumeric(centerId)) {
            throw new BadRequestException("옳지 않은 접근 방식입니다.");
        }

        return service.getItem(Long.valueOf(centerId));
    }
}
