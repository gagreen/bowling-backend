package com.gagreen.bowling.domain.score_record;

import com.gagreen.bowling.domain.score_record.dto.GameDetailDto;
import com.gagreen.bowling.domain.score_record.dto.GameRecordDto;
import com.gagreen.bowling.domain.score_record.dto.GameSearchDto;
import com.gagreen.bowling.domain.score_record.dto.GameStartDto;
import com.gagreen.bowling.domain.score_record.dto.RollAddDto;
import com.gagreen.bowling.domain.score_record.dto.RollResponseDto;
import com.gagreen.bowling.domain.score_record.game.GameVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api/games")
@Tag(name = "점수 기록", description = "볼링 게임 점수 기록 API")
public class ScoreRecordController {
    public final ScoreRecordService scoreRecordService;

    @Operation(summary = "게임 기록 (완료된 게임)", description = "완료된 게임의 모든 프레임과 롤 정보를 한 번에 기록합니다.")
    @ApiResponse(responseCode = "200", description = "기록 성공",
            content = @Content(schema = @Schema(implementation = GameDetailDto.class)))
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping
    public GameDetailDto recordGame(@Valid @RequestBody GameRecordDto dto) {
        return scoreRecordService.recordAllGame(dto);
    }

    @Operation(summary = "게임 시작", description = "새로운 게임을 시작합니다.")
    @ApiResponse(responseCode = "200", description = "게임 시작 성공",
            content = @Content(schema = @Schema(implementation = GameVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/start")
    public GameVo startGame(@Valid @RequestBody GameStartDto dto) {
        return scoreRecordService.startGame(dto);
    }

    @Operation(summary = "롤 기록", description = "게임 도중 롤 점수를 기록합니다. 프레임이 없으면 자동으로 생성됩니다.")
    @ApiResponse(responseCode = "200", description = "기록 성공",
            content = @Content(schema = @Schema(implementation = RollResponseDto.class)))
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/{gameId}/rolls")
    public RollResponseDto addRoll(@PathVariable Long gameId, @Valid @RequestBody RollAddDto dto) {
        return scoreRecordService.addRoll(gameId, dto);
    }

    @Operation(summary = "게임 종료", description = "게임을 종료하고 통계를 계산하여 저장합니다.")
    @ApiResponse(responseCode = "200", description = "게임 종료 성공",
            content = @Content(schema = @Schema(implementation = GameDetailDto.class)))
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/{gameId}/finish")
    public GameDetailDto finishGame(@PathVariable Long gameId) {
        return scoreRecordService.finishGame(gameId);
    }

    @Operation(summary = "게임 기록 조회", description = "검색 조건에 따라 게임 기록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = GameVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping
    public List<GameVo> getGames(@ModelAttribute GameSearchDto dto) {
        // 점수 조회
        return scoreRecordService.getGames(dto);
    }

    @Operation(summary = "게임 상세 조회", description = "게임 상세 정보를 조회합니다. 프레임과 롤 리스트를 포함합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = GameDetailDto.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/{gameId}")
    public GameDetailDto getGameDetail(@PathVariable Long gameId) {
        return scoreRecordService.getGameDetail(gameId);
    }

}
