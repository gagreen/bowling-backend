package com.gagreen.bowling.domain.waiting;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterService;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.domain.waiting.code.WaitingQueueStatus;
import com.gagreen.bowling.domain.waiting.dto.LaneAssignmentDto;
import com.gagreen.bowling.domain.waiting.dto.WaitingListItem;
import com.gagreen.bowling.domain.lane.LaneRepository;
import com.gagreen.bowling.domain.lane.LaneVo;
import com.gagreen.bowling.domain.lane.code.LaneStatus;
import com.gagreen.bowling.domain.waiting.dto.WaitingRegisterDto;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingService {
    private final WaitingQueueRepository waitingQueueRepository;
    private final BowlingCenterService bowlingCenterService;
    private final LaneRepository laneRepository;
    private final LaneAssignmentRepository laneAssignmentRepository;

    // 사용자 측에서 대기 등록하기
    @Transactional
    public WaitingQueueVo registerWaiting(UserVo user, WaitingRegisterDto dto) {
        BowlingCenterVo center = bowlingCenterService.getItem(dto.getCenterId(), false);

        // 같은 센터에서 WAITING 상태인 항목의 개수를 세어서 order_no 설정
        long waitingCount = waitingQueueRepository.countByCenterAndStatus(center, WaitingQueueStatus.WAITING.getCode()).orElse(0L);
        int orderNo = (int) waitingCount + 1;

        WaitingQueueVo queue = new WaitingQueueVo();
        queue.setUser(user);
        queue.setCenter(center);
        queue.setStatus(WaitingQueueStatus.WAITING);
        queue.setOrderNo(orderNo);
        queue.setPeopleCount(dto.getPeopleCount());
        queue.setCreatedAt(Instant.now());

        waitingQueueRepository.save(queue);
        return queue;
    }

    // 대기 현황 조회
    @Transactional
    public List<WaitingListItem> getCenterQueues(Long centerId) {
        log.debug("대기 현황 조회 - centerId: {}", centerId);
        // 대기 현황 조회 로직 구현
        BowlingCenterVo center = bowlingCenterService.getItem(centerId, false);

        List<WaitingListItem> queue = waitingQueueRepository.findByCenter(center);
        log.debug("대기 현황 조회 완료 - centerId: {}, 대기 인원: {}", centerId, queue.size());

        return queue;

    }

    public boolean isShouldWait(Long centerId) {
        BowlingCenterVo center = bowlingCenterService.getItem(centerId, false);
        List<LaneVo> availableLanes = laneRepository.findAvailableLanes(center);
        
        // 사용 가능한 레인이 없으면 대기 필요
        return (availableLanes == null || availableLanes.isEmpty());
    }

    public List<WaitingListItem> getMyQueueStatus() {
        UserVo user = SecurityUtil.getCurrentUser();

        // 볼링장의 레인 배정 상태 확인하면 됨
        List<WaitingListItem> queue = waitingQueueRepository.findByUser(user);

        return queue;
    }

    public boolean getMyQueueStatus(Long centerId) {
        BowlingCenterVo center = bowlingCenterService.getItem(centerId, false);
        UserVo user = SecurityUtil.getCurrentUser();

        // 볼링장의 레인 배정 상태 확인하면 됨
        Optional<WaitingListItem> queue = waitingQueueRepository.findByUserAndCenter(user, center);

        return queue.isPresent();
    }

    // 직원이 대기열의 첫 번째 항목에 레인 배정
    @Transactional
    public void assignLaneToQueue(LaneAssignmentDto dto) {
        BowlingCenterVo center = bowlingCenterService.getAssignedCenter();
        log.info("레인 배정 시도 - queueId: {}, laneId: {}", dto.getQueueId(), dto.getLaneId());

        // 대기열 조회
        WaitingQueueVo queue = waitingQueueRepository.findById(dto.getQueueId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 대기열입니다."));

        // 대기열이 해당 센터의 것인지 확인
        if (!queue.getCenter().getId().equals(center.getId())) {
            log.warn("레인 배정 실패 - 권한 없음. queueId: {}, centerId: {}, 요청 centerId: {}", 
                    dto.getQueueId(), queue.getCenter().getId(), center.getId());
            throw new BadRequestException("현재 배정된 센터의 대기열만 처리할 수 있습니다.");
        }

        // 대기 상태가 WAITING인지 확인
        if (!WaitingQueueStatus.WAITING.getCode().equals(queue.getStatus())) {
            log.warn("레인 배정 실패 - 대기 상태 아님. queueId: {}, status: {}", dto.getQueueId(), queue.getStatus());
            throw new BadRequestException("대기 중인 항목만 레인을 배정할 수 있습니다.");
        }

        // 레인 조회
        LaneVo lane = laneRepository.findById(dto.getLaneId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 레인입니다."));

        // 레인이 해당 센터의 것인지 확인
        if (!lane.getCenter().getId().equals(center.getId())) {
            log.warn("레인 배정 실패 - 레인 권한 없음. laneId: {}, centerId: {}, 요청 centerId: {}", 
                    dto.getLaneId(), lane.getCenter().getId(), center.getId());
            throw new BadRequestException("현재 배정된 센터의 레인만 사용할 수 있습니다.");
        }

        // 레인 상태 확인
        if (!LaneStatus.NORMAL.getCode().equals(lane.getStatus())) {
            log.warn("레인 배정 실패 - 레인 상태 이상. laneId: {}, status: {}", dto.getLaneId(), lane.getStatus());
            throw new BadRequestException("정상 상태의 레인만 배정할 수 있습니다.");
        }

        // 레인이 이미 배정되어 있는지 확인
        List<LaneVo> availableLanes = laneRepository.findAvailableLanes(center);
        boolean isLaneAvailable = availableLanes.stream()
                .anyMatch(l -> l.getId().equals(dto.getLaneId()));
        if (!isLaneAvailable) {
            log.warn("레인 배정 실패 - 레인 사용 중. laneId: {}", dto.getLaneId());
            throw new BadRequestException("이미 사용 중인 레인입니다.");
        }

        // 레인 배정 생성
        LaneAssignmentVo assignment = new LaneAssignmentVo();
        assignment.setLane(lane);
        assignment.setAssignedAt(Instant.now());
        laneAssignmentRepository.save(assignment);

        // 대기열 상태를 DONE으로 변경
        queue.setStatus(WaitingQueueStatus.DONE);
        waitingQueueRepository.save(queue);

        log.info("레인 배정 완료 - queueId: {}, laneId: {}, assignId: {}", 
                dto.getQueueId(), dto.getLaneId(), assignment.getId());
    }

    // 직원이 배정된 센터의 대기열 조회
    @Transactional(readOnly = true)
    public List<WaitingListItem> getMyCenterQueues() {
        log.debug("센터 대기열 조회 시작");
        BowlingCenterVo center = bowlingCenterService.getAssignedCenter();
        List<WaitingListItem> queues = waitingQueueRepository.findByCenter(center);
        log.debug("센터 대기열 조회 완료 - centerId: {}, 대기 건수: {}", center.getId(), queues.size());
        return queues;
    }

}
