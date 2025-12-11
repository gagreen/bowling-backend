package com.gagreen.bowling.domain.favorite;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api")
@Tag(name = "즐겨찾기", description = "볼링장 즐겨찾기 관리 API")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Operation(summary = "즐겨찾기 추가", description = "볼링장을 즐겨찾기에 추가합니다.")
    @ApiResponse(responseCode = "200", description = "추가 성공")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/centers/{centerId}/favorites")
    public void add(@PathVariable Long centerId) {
        UserVo user = SecurityUtil.getCurrentUser();

        if (favoriteService.isRegistered(user, centerId)) {
            throw new BadRequestException("이미 즐겨찾기에 추가된 볼링장입니다.");
        }
        // 즐겨찾기 추가
        favoriteService.addFavorite(user, centerId);

        return;
    }

    @Operation(summary = "즐겨찾기 제거", description = "즐겨찾기에서 볼링장을 제거합니다.")
    @ApiResponse(responseCode = "200", description = "제거 성공")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/centers/{centerId}/favorites")
    public void delete(@PathVariable Long centerId) {
        UserVo user = SecurityUtil.getCurrentUser();

        if (!favoriteService.isRegistered(user, centerId)) {
            throw new BadRequestException("즐겨찾기에 등록되지 않은 볼링장입니다.");
        }

        // 즐겨찾기 제거
        favoriteService.deleteFavorite(user, centerId);
    }

    @Operation(summary = "즐겨찾기 목록 조회", description = "즐겨찾기한 볼링장 목록을 페이지네이션으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BowlingCenterVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/favorites")
    public Page<BowlingCenterVo> getFavorites(Pageable dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        // 즐겨찾기한 볼링장 목록 조회
        Page<BowlingCenterVo> favorites = favoriteService.getFavoritesByUser(user, dto);

        return favorites;
    }


}
