package com.gagreen.bowling.domain.lane;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterService;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.lane.code.LaneStatus;
import com.gagreen.bowling.domain.lane.dto.LaneCreateDto;
import com.gagreen.bowling.domain.lane.dto.LaneStatusUpdateDto;
import com.gagreen.bowling.domain.waiting.LaneAssignmentRepository;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LaneService {

    private final LaneRepository laneRepository;
    private final BowlingCenterService bowlingCenterService;
    private final LaneAssignmentRepository laneAssignmentRepository;

    @Transactional(readOnly = true)
    public List<LaneVo> getMyCenterLanes() {
        log.debug("센터 레인 목록 조회 시작");
        BowlingCenterVo center = bowlingCenterService.getAssignedCenter();
        List<LaneVo> lanes = laneRepository.findByCenter(center);
        
        // 각 레인의 배정 상태 확인 및 할당된 대기열 정보 조회
        lanes.forEach(lane -> {
            boolean isAssigned = laneAssignmentRepository.existsByLaneAndFinishedAtIsNull(lane);
            lane.setIsAssigned(isAssigned);
            
            // 할당된 대기열 정보 조회
            if (isAssigned) {
                Optional<com.gagreen.bowling.domain.waiting.LaneAssignmentVo> assignment = 
                    laneAssignmentRepository.findByLaneAndFinishedAtIsNull(lane);
                assignment.ifPresent(a -> lane.setAssignedQueue(a.getQueue()));
            }
        });
        
        log.debug("센터 레인 목록 조회 완료 - centerId: {}, 레인 수: {}", center.getId(), lanes.size());
        return lanes;
    }

    @Transactional
    public LaneVo createLane(LaneCreateDto dto) {
        log.info("레인 생성 시도 - laneNumber: {}, status: {}", dto.getLaneNumber(), dto.getStatus());
        BowlingCenterVo center = bowlingCenterService.getAssignedCenter();

        // 같은 센터에 동일한 레인 번호가 있는지 확인
        List<LaneVo> existingLanes = laneRepository.findByCenter(center);
        boolean isDuplicate = existingLanes.stream()
                .anyMatch(lane -> lane.getLaneNumber() != null && 
                        lane.getLaneNumber().equals(dto.getLaneNumber()));
        
        if (isDuplicate) {
            log.warn("레인 생성 실패 - 중복된 레인 번호. centerId: {}, laneNumber: {}", 
                    center.getId(), dto.getLaneNumber());
            throw new BadRequestException("이미 존재하는 레인 번호입니다.");
        }

        LaneVo lane = new LaneVo();
        lane.setCenter(center);
        lane.setLaneNumber(dto.getLaneNumber());
        lane.setStatus(dto.getStatus() != null ? dto.getStatus() : LaneStatus.NORMAL);

        LaneVo savedLane = laneRepository.save(lane);
        log.info("레인 생성 완료 - laneId: {}, laneNumber: {}, status: {}", 
                savedLane.getId(), savedLane.getLaneNumber(), savedLane.getStatus());
        return savedLane;
    }

    @Transactional
    public LaneVo updateLaneStatus(LaneStatusUpdateDto dto) {
        log.info("레인 상태 업데이트 시도 - laneId: {}, status: {}", dto.getLaneId(), dto.getStatus());
        BowlingCenterVo center = bowlingCenterService.getAssignedCenter();

        Long laneId = dto.getLaneId();
        LaneStatus status = dto.getStatus();

        LaneVo lane = laneRepository.findById(laneId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 레인입니다."));

        if (!lane.getCenter().getId().equals(center.getId())) {
            log.warn("레인 상태 업데이트 실패 - 권한 없음. laneId: {}, centerId: {}, 요청 centerId: {}", 
                    laneId, lane.getCenter().getId(), center.getId());
            throw new BadRequestException("현재 배정된 센터의 레인만 수정할 수 있습니다.");
        }

        LaneStatus oldStatus = LaneStatus.fromCode(lane.getStatus());
        lane.setStatus(status);
        LaneVo updatedLane = laneRepository.save(lane);
        log.info("레인 상태 업데이트 완료 - laneId: {}, 이전 상태: {}, 새 상태: {}", 
                laneId, oldStatus, status);
        return updatedLane;
    }
}
