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
            
            // 프레임별 핀 수 검증
            validateFramePins(frameDto.getFrameNumber(), frameDto.getRolls());
            
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
        
        // 기존 롤 조회
        List<RollVo> existingRolls = rollRepository.findByFrameOrderByRollNumber(frame);
        
        // 프레임별 핀 수 검증 (새로운 롤 포함)
        validateFramePinsWithNewRoll(frame.getFrameNumber(), existingRolls, dto.getRollNumber(), dto.getPins());
        
        // 롤 생성
        RollVo roll = new RollVo();
        roll.setFrame(frame);
        roll.setRollNumber(dto.getRollNumber());
        roll.setPins(dto.getPins());
        rollRepository.save(roll);
        
        // 게임 업데이트 시간 갱신
        game.setUpdatedAt(Instant.now());
        gameRepository.save(game);
        
        // 롤 추가 시마다 점수 계산 및 업데이트
        calculateAndSaveGameStatistics(game);
        
        return RollResponseDto.from(roll, frame);
    }

    /**
     * 정확한 볼링 점수 계산 (스트라이크/스페어 보너스 포함)
     */
    private Integer calculateBowlingScore(List<FrameVo> frames, Map<Long, List<RollVo>> frameRollsMap) {
        int totalScore = 0;
        
        for (int i = 0; i < frames.size(); i++) {
            int frameScore = calculateFrameScore(frames, frameRollsMap, i);
            if (frameScore >= 0) {
                totalScore += frameScore;
            }
        }
        
        return totalScore;
    }
    
    /**
     * 프레임별 누적 점수 계산 (스트라이크/스페어 보너스 포함)
     * @return Map<frameId, 누적 점수> - 보너스 미확정 프레임은 null
     */
    private Map<Long, Integer> calculateFrameScores(List<FrameVo> frames, Map<Long, List<RollVo>> frameRollsMap) {
        Map<Long, Integer> frameScores = new java.util.HashMap<>();
        int cumulativeScore = 0;
        
        for (int i = 0; i < frames.size(); i++) {
            FrameVo frame = frames.get(i);
            int frameScore = calculateFrameScore(frames, frameRollsMap, i);
            
            if (frameScore >= 0) {
                // 보너스가 확정된 경우 누적 점수 계산
                cumulativeScore += frameScore;
                frameScores.put(frame.getId(), cumulativeScore);
            } else {
                // 보너스가 아직 확정되지 않은 경우 null 저장
                frameScores.put(frame.getId(), null);
            }
        }
        
        return frameScores;
    }
    
    /**
     * 개별 프레임의 점수 계산 (보너스 포함)
     * @return 프레임 점수, 보너스 미확정 시 -1 반환
     */
    private int calculateFrameScore(List<FrameVo> frames, Map<Long, List<RollVo>> frameRollsMap, int frameIndex) {
        FrameVo frame = frames.get(frameIndex);
        List<RollVo> rolls = frameRollsMap.get(frame.getId());
        
        if (rolls == null || rolls.isEmpty()) {
            return -1;
        }
        
        int frameNumber = frame.getFrameNumber();
        RollVo firstRoll = rolls.get(0);
        RollVo secondRoll = rolls.size() > 1 ? rolls.get(1) : null;
        RollVo thirdRoll = rolls.size() > 2 ? rolls.get(2) : null;
        
        int firstPins = firstRoll.getPins() != null ? firstRoll.getPins() : 0;
        int secondPins = secondRoll != null && secondRoll.getPins() != null ? secondRoll.getPins() : 0;
        int thirdPins = thirdRoll != null && thirdRoll.getPins() != null ? thirdRoll.getPins() : 0;
        
        // 10프레임 특수 처리
        if (frameNumber == 10) {
            if (firstPins == 10) {
                // 스트라이크: 10 + 두 번째 롤 + 세 번째 롤
                if (secondRoll != null && thirdRoll != null) {
                    return 10 + secondPins + thirdPins;
                } else if (secondRoll != null) {
                    // 진행 중: 두 번째 롤만 있음
                    return -1; // 보너스 미확정
                } else {
                    // 진행 중: 첫 롤만 있음
                    return -1; // 보너스 미확정
                }
            } else if (firstPins + secondPins == 10) {
                // 스페어: 10 + 세 번째 롤
                if (thirdRoll != null) {
                    return 10 + thirdPins;
                } else {
                    // 진행 중: 보너스 롤 없음
                    return -1; // 보너스 미확정
                }
            } else {
                // 일반: 두 롤 합
                return firstPins + secondPins;
            }
        } else {
            // 1-9프레임 처리
            if (firstPins == 10) {
                // 스트라이크: 10 + 다음 프레임의 첫 2개 롤
                int bonusScore = getNextTwoRollsScore(frames, frameRollsMap, frameIndex);
                if (bonusScore >= 0) {
                    return 10 + bonusScore;
                } else {
                    // 보너스 롤이 아직 없음 (진행 중)
                    return -1; // 보너스 미확정
                }
            } else if (firstPins < 10 && (firstPins + secondPins) == 10) {
                // 스페어: 10 + 다음 프레임의 첫 롤
                int bonusScore = getNextOneRollScore(frames, frameRollsMap, frameIndex);
                if (bonusScore >= 0) {
                    return 10 + bonusScore;
                } else {
                    // 보너스 롤이 아직 없음 (진행 중)
                    return -1; // 보너스 미확정
                }
            } else {
                // 일반: 두 롤 합
                return firstPins + secondPins;
            }
        }
    }
    
    /**
     * 다음 프레임의 첫 2개 롤 점수 반환 (스트라이크 보너스용)
     */
    private int getNextTwoRollsScore(List<FrameVo> frames, Map<Long, List<RollVo>> frameRollsMap, int currentIndex) {
        if (currentIndex + 1 >= frames.size()) {
            return -1; // 다음 프레임이 없음
        }
        
        FrameVo nextFrame = frames.get(currentIndex + 1);
        List<RollVo> nextRolls = frameRollsMap.get(nextFrame.getId());
        
        if (nextRolls == null || nextRolls.isEmpty()) {
            return -1;
        }
        
        RollVo nextFirstRoll = nextRolls.get(0);
        int nextFirstPins = nextFirstRoll.getPins() != null ? nextFirstRoll.getPins() : 0;
        
        // 다음 프레임이 스트라이크인 경우
        if (nextFirstPins == 10) {
            // 다음 다음 프레임의 첫 롤도 필요
            if (currentIndex + 2 < frames.size()) {
                FrameVo nextNextFrame = frames.get(currentIndex + 2);
                List<RollVo> nextNextRolls = frameRollsMap.get(nextNextFrame.getId());
                if (nextNextRolls != null && !nextNextRolls.isEmpty()) {
                    RollVo nextNextFirstRoll = nextNextRolls.get(0);
                    int nextNextFirstPins = nextNextFirstRoll.getPins() != null ? nextNextFirstRoll.getPins() : 0;
                    return 10 + nextNextFirstPins;
                }
            }
            // 10프레임인 경우 세 번째 롤도 확인
            if (nextFrame.getFrameNumber() == 10 && nextRolls.size() > 1) {
                RollVo nextSecondRoll = nextRolls.get(1);
                int nextSecondPins = nextSecondRoll.getPins() != null ? nextSecondRoll.getPins() : 0;
                return 10 + nextSecondPins;
            }
            return -1; // 보너스 롤이 아직 없음
        } else {
            // 다음 프레임이 일반 또는 스페어
            if (nextRolls.size() > 1) {
                RollVo nextSecondRoll = nextRolls.get(1);
                int nextSecondPins = nextSecondRoll.getPins() != null ? nextSecondRoll.getPins() : 0;
                return nextFirstPins + nextSecondPins;
            }
            return -1; // 두 번째 롤이 아직 없음
        }
    }
    
    /**
     * 다음 프레임의 첫 롤 점수 반환 (스페어 보너스용)
     */
    private int getNextOneRollScore(List<FrameVo> frames, Map<Long, List<RollVo>> frameRollsMap, int currentIndex) {
        if (currentIndex + 1 >= frames.size()) {
            return -1; // 다음 프레임이 없음
        }
        
        FrameVo nextFrame = frames.get(currentIndex + 1);
        List<RollVo> nextRolls = frameRollsMap.get(nextFrame.getId());
        
        if (nextRolls == null || nextRolls.isEmpty()) {
            return -1;
        }
        
        RollVo nextFirstRoll = nextRolls.get(0);
        return nextFirstRoll.getPins() != null ? nextFirstRoll.getPins() : 0;
    }
    
    /**
     * 프레임 완료 여부 판단
     * @return true: 완료, false: 진행 중
     */
    private boolean isFrameCompleted(List<FrameVo> frames, Map<Long, List<RollVo>> frameRollsMap, int frameIndex) {
        FrameVo frame = frames.get(frameIndex);
        List<RollVo> rolls = frameRollsMap.get(frame.getId());
        
        if (rolls == null || rolls.isEmpty()) {
            return false;
        }
        
        int frameNumber = frame.getFrameNumber();
        RollVo firstRoll = rolls.get(0);
        RollVo secondRoll = rolls.size() > 1 ? rolls.get(1) : null;
        RollVo thirdRoll = rolls.size() > 2 ? rolls.get(2) : null;
        
        int firstPins = firstRoll.getPins() != null ? firstRoll.getPins() : 0;
        int secondPins = secondRoll != null && secondRoll.getPins() != null ? secondRoll.getPins() : 0;
        
        // 10프레임 특수 처리
        if (frameNumber == 10) {
            if (firstPins == 10) {
                // 스트라이크: 세 롤 모두 완료되어야 함
                return secondRoll != null && thirdRoll != null;
            } else if (firstPins + secondPins == 10) {
                // 스페어: 세 롤 모두 완료되어야 함
                return thirdRoll != null;
            } else {
                // 일반: 두 롤 완료
                return secondRoll != null;
            }
        } else {
            // 1-9프레임 처리
            if (firstPins == 10) {
                // 스트라이크: 다음 프레임의 첫 2개 롤 확정되어야 함
                int bonusScore = getNextTwoRollsScore(frames, frameRollsMap, frameIndex);
                return bonusScore >= 0;
            } else if (firstPins < 10 && (firstPins + secondPins) == 10) {
                // 스페어: 다음 프레임의 첫 롤 확정되어야 함
                int bonusScore = getNextOneRollScore(frames, frameRollsMap, frameIndex);
                return bonusScore >= 0;
            } else {
                // 일반: 두 롤 완료
                return secondRoll != null;
            }
        }
    }

    /**
     * 프레임별 핀 수 검증
     */
    private void validateFramePins(Integer frameNumber, List<RollRecordDto> rolls) {
        if (rolls == null || rolls.isEmpty()) {
            return;
        }
        
        // 롤 번호 순서대로 정렬
        List<RollRecordDto> sortedRolls = rolls.stream()
                .sorted(Comparator.comparing(RollRecordDto::getRollNumber))
                .collect(Collectors.toList());
        
        RollRecordDto firstRoll = sortedRolls.get(0);
        RollRecordDto secondRoll = sortedRolls.size() > 1 ? sortedRolls.get(1) : null;
        RollRecordDto thirdRoll = sortedRolls.size() > 2 ? sortedRolls.get(2) : null;
        
        int firstPins = firstRoll.getPins() != null ? firstRoll.getPins() : 0;
        int secondPins = secondRoll != null && secondRoll.getPins() != null ? secondRoll.getPins() : 0;
        int thirdPins = thirdRoll != null && thirdRoll.getPins() != null ? thirdRoll.getPins() : 0;
        
        // 10프레임 특수 처리
        if (frameNumber == 10) {
            // 각 롤의 핀 수 검증 (10을 넘을 수 없음)
            if (firstPins > 10) {
                throw new BadRequestException("프레임 " + frameNumber + "의 첫 번째 롤 핀 수는 10을 넘을 수 없습니다.");
            }
            if (secondRoll != null && secondPins > 10) {
                throw new BadRequestException("프레임 " + frameNumber + "의 두 번째 롤 핀 수는 10을 넘을 수 없습니다.");
            }
            if (thirdRoll != null && thirdPins > 10) {
                throw new BadRequestException("프레임 " + frameNumber + "의 세 번째 롤 핀 수는 10을 넘을 수 없습니다.");
            }
            
            // 10프레임에서 스트라이크/스페어인 경우 보너스 롤이 있으므로 첫 두 롤 합 제한 없음
            if (firstPins == 10) {
                // 스트라이크: 첫 롤 10, 두 번째 롤도 10 가능 (합 20 가능), 세 번째 롤도 가능
                // 검증 완료 (각 롤만 10 이하)
            } else if (firstPins + secondPins == 10) {
                // 스페어: 첫 두 롤 합이 정확히 10, 세 번째 롤 가능
                // 검증 완료 (첫 두 롤 합이 10이므로 문제 없음)
            } else {
                // 일반: 첫 두 롤 합이 10 미만, 세 번째 롤 없음
                if (firstPins + secondPins > 10) {
                    throw new BadRequestException("프레임 " + frameNumber + "의 첫 두 롤 핀 수 합은 10을 넘을 수 없습니다.");
                }
                if (thirdRoll != null) {
                    throw new BadRequestException("프레임 " + frameNumber + "은 일반 프레임이므로 세 번째 롤이 있을 수 없습니다.");
                }
            }
        } else {
            // 1-9프레임 처리
            if (firstPins == 10) {
                // 스트라이크: 첫 롤 10, 두 번째 롤 없음
                if (secondRoll != null) {
                    throw new BadRequestException("프레임 " + frameNumber + "은 스트라이크이므로 두 번째 롤이 있을 수 없습니다.");
                }
                if (thirdRoll != null) {
                    throw new BadRequestException("프레임 " + frameNumber + "은 스트라이크이므로 세 번째 롤이 있을 수 없습니다.");
                }
            } else {
                // 일반 또는 스페어: 첫 두 롤 합이 최대 10
                if (firstPins > 10 || (secondRoll != null && secondRoll.getPins() > 10)) {
                    throw new BadRequestException("프레임 " + frameNumber + "의 각 롤 핀 수는 10을 넘을 수 없습니다.");
                }
                if (secondRoll != null && firstPins + secondPins > 10) {
                    throw new BadRequestException("프레임 " + frameNumber + "의 첫 두 롤 핀 수 합은 10을 넘을 수 없습니다.");
                }
                if (thirdRoll != null) {
                    throw new BadRequestException("프레임 " + frameNumber + "은 10프레임이 아니므로 세 번째 롤이 있을 수 없습니다.");
                }
            }
        }
    }
    
    /**
     * 프레임별 핀 수 검증 (addRoll용 - 기존 롤과 새로운 롤 포함)
     */
    private void validateFramePinsWithNewRoll(Integer frameNumber, List<RollVo> existingRolls, Integer newRollNumber, Integer newPins) {
        // 기존 롤과 새로운 롤을 합쳐서 검증
        List<RollRecordDto> allRolls = new java.util.ArrayList<>();
        
        // 기존 롤 추가
        for (RollVo existingRoll : existingRolls) {
            RollRecordDto rollDto = new RollRecordDto();
            rollDto.setRollNumber(existingRoll.getRollNumber());
            rollDto.setPins(existingRoll.getPins());
            allRolls.add(rollDto);
        }
        
        // 새로운 롤 추가
        RollRecordDto newRollDto = new RollRecordDto();
        newRollDto.setRollNumber(newRollNumber);
        newRollDto.setPins(newPins);
        allRolls.add(newRollDto);
        
        // 검증 수행
        validateFramePins(frameNumber, allRolls);
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
        
        // 정확한 볼링 점수 계산
        Integer totalScore = calculateBowlingScore(frames, frameRollsMap);
        
        // 프레임별 누적 점수 계산
        Map<Long, Integer> frameScores = calculateFrameScores(frames, frameRollsMap);
        
        int strikeCount = 0;
        int spareCount = 0;
        int gutterCount = 0;
        
        // 각 프레임별로 통계 계산 및 프레임 점수 저장
        for (int i = 0; i < frames.size(); i++) {
            FrameVo frame = frames.get(i);
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
            }
            // 스페어 체크 (첫 롤이 10 미만이고 두 롤 합이 10)
            else if (firstPins < 10 && (firstPins + secondPins) == 10) {
                spareCount++;
            }
            // 게터 체크 (두 롤 합이 10 미만)
            else if (firstPins + secondPins < 10) {
                gutterCount++;
            }
            
            // 프레임별 누적 점수 저장
            frame.setFrameScore(frameScores.get(frame.getId()));
            
            // 프레임 완료 여부 설정
            boolean completed = isFrameCompleted(frames, frameRollsMap, i);
            frame.setIsCompleted(completed);
        }
        
        // 프레임 점수 저장
        frameRepository.saveAll(frames);
        
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
