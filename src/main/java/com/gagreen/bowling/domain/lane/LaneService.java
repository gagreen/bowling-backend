package com.gagreen.bowling.domain.lane;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.lane.code.LaneStatus;
import com.gagreen.bowling.domain.lane.dto.LaneStatusUpdateDto;
import com.gagreen.bowling.domain.staff.StaffRepository;
import com.gagreen.bowling.domain.staff.StaffVo;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LaneService {

    private final LaneRepository laneRepository;
    private final StaffRepository staffRepository;

    @Transactional(readOnly = true)
    public List<LaneVo> getMyCenterLanes() {
        log.debug("센터 레인 목록 조회 시작");
        BowlingCenterVo center = getAssignedCenter();
        List<LaneVo> lanes = laneRepository.findByCenter(center);
        log.debug("센터 레인 목록 조회 완료 - centerId: {}, 레인 수: {}", center.getId(), lanes.size());
        return lanes;
    }

    @Transactional
    public LaneVo updateLaneStatus(LaneStatusUpdateDto dto) {
        log.info("레인 상태 업데이트 시도 - laneId: {}, status: {}", dto.getLaneId(), dto.getStatus());
        BowlingCenterVo center = getAssignedCenter();

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

    private BowlingCenterVo getAssignedCenter() {
        StaffVo staff = SecurityUtil.getCurrentStaff();
        StaffVo persistedStaff = staffRepository.findById(staff.getId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 스태프입니다."));

        if (persistedStaff.getCenter() == null) {
            throw new BadRequestException("센터에 배정되지 않은 스태프입니다.");
        }

        return persistedStaff.getCenter();
    }
}
