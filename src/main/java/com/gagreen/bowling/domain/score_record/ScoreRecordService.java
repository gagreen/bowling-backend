package com.gagreen.bowling.domain.score_record;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterRepository;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.score_record.dto.FrameDetailDto;
import com.gagreen.bowling.domain.score_record.dto.FrameRecordDto;
import com.gagreen.bowling.domain.score_record.dto.GameDetailDto;
import com.gagreen.bowling.domain.score_record.dto.GameRecordDto;
import com.gagreen.bowling.domain.score_record.dto.GameSearchDto;
import com.gagreen.bowling.domain.score_record.dto.GameStartDto;
import com.gagreen.bowling.domain.score_record.dto.RollAddDto;
import com.gagreen.bowling.domain.score_record.dto.RollDetailDto;
import com.gagreen.bowling.domain.score_record.dto.RollRecordDto;
import com.gagreen.bowling.domain.score_record.dto.RollResponseDto;
import com.gagreen.bowling.domain.score_record.frame.FrameRepository;
import com.gagreen.bowling.domain.score_record.frame.FrameVo;
import com.gagreen.bowling.domain.score_record.game.GameRepository;
import com.gagreen.bowling.domain.score_record.game.GameVo;
import com.gagreen.bowling.domain.score_record.roll.RollRepository;
import com.gagreen.bowling.domain.score_record.roll.RollVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreRecordService {
    public final GameRepository gameRepository;
    public final FrameRepository frameRepository;
    public final RollRepository rollRepository;
    public final BowlingCenterRepository bowlingCenterRepository;

    public void recordScore() {
        // 점수 기록
    }

    @Transactional
    public GameDetailDto recordAllGame(GameRecordDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        
        // 볼링장 조회
        BowlingCenterVo center = bowlingCenterRepository.findById(dto.getCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 볼링장입니다."));
        
        // 게임 생성
        Instant now = Instant.now();
        GameVo game = new GameVo();
        game.setUser(user);
        game.setCenter(center);
        game.setCreatedAt(now);
        game.setUpdatedAt(now);
        gameRepository.save(game);
        
        // 프레임 및 롤 저장
        for (FrameRecordDto frameDto : dto.getFrames()) {
            // 중복 검증: 같은 게임에 같은 frame_number가 이미 존재하는지 확인
            if (frameRepository.existsByGameAndFrameNumber(game, frameDto.getFrameNumber())) {
                throw new BadRequestException("해당 게임에 이미 같은 프레임 번호가 존재합니다.");
            }
            
            FrameVo frame = new FrameVo();
            frame.setGame(game);
            frame.setFrameNumber(frameDto.getFrameNumber());
            FrameVo savedFrame = frameRepository.save(frame);
            
            for (RollRecordDto rollDto : frameDto.getRolls()) {
                // 중복 검증: 같은 프레임에 같은 roll_number가 이미 존재하는지 확인
                if (rollRepository.existsByFrameAndRollNumber(savedFrame, rollDto.getRollNumber())) {
                    throw new BadRequestException("해당 프레임에 이미 같은 롤 번호가 존재합니다.");
                }
                
                RollVo roll = new RollVo();
                roll.setFrame(savedFrame);
                roll.setRollNumber(rollDto.getRollNumber());
                roll.setPins(rollDto.getPins());
                rollRepository.save(roll);
            }
        }
        
        // 통계 계산 및 저장
        return calculateAndSaveGameStatistics(game);
    }

    public List<GameVo> getGames(GameSearchDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        // QueryDSL을 사용하여 join으로 한 번에 조회 (로그인한 사용자의 게임만)
        return gameRepository.search(dto, user);
    }

    public GameDetailDto getGameDetail(Long id) {
        UserVo user = SecurityUtil.getCurrentUser();
        
        // 게임 조회 (user, center 포함)
        GameVo gameVo = gameRepository.findByIdWithUserAndCenter(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 게임입니다."));

        // 프레임 목록 조회
        List<FrameVo> frames = frameRepository.findByGameOrderByFrameNumber(gameVo);
        
        // 각 프레임의 롤 정보를 조회하여 DTO로 변환
        List<FrameDetailDto> frameDtos = frames.stream()
                .map(frame -> {
                    List<RollVo> rolls = rollRepository.findByFrameOrderByRollNumber(frame);
                    List<RollDetailDto> rollDtos = rolls.stream()
                            .map(RollDetailDto::from)
                            .collect(Collectors.toList());
                    return FrameDetailDto.from(frame, rollDtos);
                })
                .collect(Collectors.toList());

        return GameDetailDto.from(gameVo, frameDtos);
    }

    @Transactional
    public GameVo startGame(GameStartDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        
        // 볼링장 조회
        BowlingCenterVo center = bowlingCenterRepository.findById(dto.getCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 볼링장입니다."));
        
        // 게임 생성
        Instant now = Instant.now();
        GameVo game = new GameVo();
        game.setUser(user);
        game.setCenter(center);
        game.setCreatedAt(now);
        game.setUpdatedAt(now);
        
        return gameRepository.save(game);
    }

    @Transactional
    public RollResponseDto addRoll(Long gameId, RollAddDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        
        // 게임 조회 및 권한 확인
        GameVo game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 게임입니다."));
        
        if (!game.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("본인의 게임만 수정할 수 있습니다.");
        }
        
        // 프레임 조회 또는 생성
        FrameVo frame = frameRepository.findByGameAndFrameNumber(game, dto.getFrameNumber())
                .orElseGet(() -> {
                    FrameVo newFrame = new FrameVo();
                    newFrame.setGame(game);
                    newFrame.setFrameNumber(dto.getFrameNumber());
                    return frameRepository.save(newFrame);
                });
        
        // 중복 검증: 같은 프레임에 같은 roll_number가 이미 존재하는지 확인
        if (rollRepository.existsByFrameAndRollNumber(frame, dto.getRollNumber())) {
            throw new BadRequestException("해당 프레임에 이미 같은 롤 번호가 존재합니다.");
        }
        
        // 롤 생성
        RollVo roll = new RollVo();
        roll.setFrame(frame);
        roll.setRollNumber(dto.getRollNumber());
        roll.setPins(dto.getPins());
        rollRepository.save(roll);
        
        // 게임 업데이트 시간 갱신
        game.setUpdatedAt(Instant.now());
        gameRepository.save(game);
        
        return RollResponseDto.from(roll, frame);
    }

    /**
     * 게임 통계를 계산하고 저장
     */
    @Transactional
    public GameDetailDto calculateAndSaveGameStatistics(GameVo game) {
        // 프레임 목록 조회
        List<FrameVo> frames = frameRepository.findByGameOrderByFrameNumber(game);
        
        // 각 프레임의 롤 정보를 맵으로 구성
        Map<Long, List<RollVo>> frameRollsMap = frames.stream()
                .collect(Collectors.toMap(
                        FrameVo::getId,
                        frame -> rollRepository.findByFrameOrderByRollNumber(frame)
                ));
        
        int totalScore = 0;
        int strikeCount = 0;
        int spareCount = 0;
        int gutterCount = 0;
        
        // 각 프레임별로 통계 계산
        for (FrameVo frame : frames) {
            List<RollVo> rolls = frameRollsMap.get(frame.getId());
            if (rolls == null || rolls.isEmpty()) {
                continue;
            }
            
            RollVo firstRoll = rolls.get(0);
            RollVo secondRoll = rolls.size() > 1 ? rolls.get(1) : null;
            
            int firstPins = firstRoll.getPins() != null ? firstRoll.getPins() : 0;
            int secondPins = secondRoll != null && secondRoll.getPins() != null ? secondRoll.getPins() : 0;
            
            // 스트라이크 체크 (첫 롤이 10핀)
            if (firstPins == 10) {
                strikeCount++;
                totalScore += firstPins;
            }
            // 스페어 체크 (첫 롤이 10 미만이고 두 롤 합이 10)
            else if (firstPins < 10 && (firstPins + secondPins) == 10) {
                spareCount++;
                totalScore += firstPins + secondPins;
            }
            // 일반 점수
            else {
                totalScore += firstPins + secondPins;
                // 게터 체크 (두 롤 합이 10 미만)
                if (firstPins + secondPins < 10) {
                    gutterCount++;
                }
            }
        }
        
        // 통계 저장
        game.setTotalScore(totalScore);
        game.setStrikeCount(strikeCount);
        game.setSpareCount(spareCount);
        game.setGutterCount(gutterCount);
        gameRepository.save(game);
        
        // 프레임과 롤 정보를 DTO로 변환
        List<FrameDetailDto> frameDtos = frames.stream()
                .map(frame -> {
                    List<RollVo> rolls = frameRollsMap.get(frame.getId());
                    List<RollDetailDto> rollDtos = rolls.stream()
                            .map(RollDetailDto::from)
                            .collect(Collectors.toList());
                    return FrameDetailDto.from(frame, rollDtos);
                })
                .collect(Collectors.toList());

        return GameDetailDto.from(game, frameDtos);
    }

    /**
     * 게임 종료 및 통계 계산
     */
    @Transactional
    public GameDetailDto finishGame(Long gameId) {
        UserVo user = SecurityUtil.getCurrentUser();
        
        // 게임 조회 및 권한 확인
        GameVo game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 게임입니다."));
        
        if (!game.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("본인의 게임만 종료할 수 있습니다.");
        }
        
        // 통계 계산 및 저장
        return calculateAndSaveGameStatistics(game);
    }
}
