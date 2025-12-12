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

        // orderNo 순서 확인: 더 작은 orderNo를 가진 WAITING 상태의 대기열이 있는지 확인
        List<WaitingQueueVo> waitingQueues = waitingQueueRepository.findByCenterAndStatus(
                center, WaitingQueueStatus.WAITING.getCode());
        boolean hasEarlierQueue = waitingQueues.stream()
                .filter(wq -> !wq.getId().equals(queue.getId())) // 현재 대기열 제외
                .anyMatch(wq -> wq.getOrderNo() != null && 
                        queue.getOrderNo() != null && 
                        wq.getOrderNo() < queue.getOrderNo());
        
        if (hasEarlierQueue) {
            log.warn("레인 배정 실패 - 순서 위반. queueId: {}, orderNo: {}, 더 작은 orderNo가 존재", 
                    dto.getQueueId(), queue.getOrderNo());
            throw new BadRequestException("더 먼저 대기한 항목이 있습니다. 순서대로 배정해 주세요.");
        }

        // 레인 배정 생성
        LaneAssignmentVo assignment = new LaneAssignmentVo();
        assignment.setLane(lane);
        assignment.setQueue(queue);
        assignment.setAssignedAt(Instant.now());
        laneAssignmentRepository.save(assignment);

        // 대기열 상태를 DONE으로 변경
        queue.setStatus(WaitingQueueStatus.DONE);
        waitingQueueRepository.save(queue);

        log.info("레인 배정 완료 - queueId: {}, laneId: {}, assignId: {}", 
                dto.getQueueId(), dto.getLaneId(), assignment.getId());
    }

    /**
     * 레인 배정을 종료하고 finishedAt을 설정합니다.
     * @param dto 레인 배정 종료 요청 DTO
     * @throws ResourceNotFoundException 레인 또는 배정이 존재하지 않는 경우
     * @throws BadRequestException 레인이 배정되지 않았거나 이미 종료된 경우
     */
    @Transactional
    public void finishLaneAssignment(com.gagreen.bowling.domain.waiting.dto.LaneAssignmentFinishDto dto) {
        BowlingCenterVo center = bowlingCenterService.getAssignedCenter();
        log.info("레인 배정 종료 시도 - laneId: {}", dto.getLaneId());

        // 레인 조회
        LaneVo lane = laneRepository.findById(dto.getLaneId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 레인입니다."));

        // 레인이 해당 센터의 것인지 확인
        if (!lane.getCenter().getId().equals(center.getId())) {
            log.warn("레인 배정 종료 실패 - 레인 권한 없음. laneId: {}, centerId: {}, 요청 centerId: {}", 
                    dto.getLaneId(), lane.getCenter().getId(), center.getId());
            throw new BadRequestException("현재 배정된 센터의 레인만 처리할 수 있습니다.");
        }

        // 현재 배정된 레인 배정 조회
        Optional<LaneAssignmentVo> assignmentOpt = laneAssignmentRepository.findByLaneAndFinishedAtIsNull(lane);
        if (assignmentOpt.isEmpty()) {
            log.warn("레인 배정 종료 실패 - 배정되지 않음. laneId: {}", dto.getLaneId());
            throw new BadRequestException("배정되지 않은 레인입니다.");
        }

        LaneAssignmentVo assignment = assignmentOpt.get();
        
        // 이미 종료된 경우 확인 (이중 체크)
        if (assignment.getFinishedAt() != null) {
            log.warn("레인 배정 종료 실패 - 이미 종료됨. laneId: {}, assignId: {}", 
                    dto.getLaneId(), assignment.getId());
            throw new BadRequestException("이미 종료된 레인 배정입니다.");
        }

        // finishedAt 설정
        assignment.setFinishedAt(Instant.now());
        laneAssignmentRepository.save(assignment);

        log.info("레인 배정 종료 완료 - laneId: {}, assignId: {}", 
                dto.getLaneId(), assignment.getId());
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

    // 사용자가 대기 취소
    @Transactional
    public void cancelWaiting(UserVo user, Long centerId) {
        log.info("대기 취소 시도 - userId: {}, centerId: {}", user.getId(), centerId);

        BowlingCenterVo center = bowlingCenterService.getItem(centerId, false);

        // 해당 센터에서 사용자가 등록한 WAITING 상태의 대기열 조회
        Optional<WaitingListItem> queueItem = waitingQueueRepository.findByUserAndCenter(user, center);
        
        if (queueItem.isEmpty()) {
            throw new ResourceNotFoundException("등록된 대기열이 없습니다.");
        }

        // 실제 엔티티 조회
        WaitingQueueVo queue = waitingQueueRepository.findById(queueItem.get().getQueueId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 대기열입니다."));

        // 본인이 등록한 대기열인지 확인
        if (!queue.getUser().getId().equals(user.getId())) {
            log.warn("대기 취소 실패 - 권한 없음. userId: {}, queueId: {}, 등록자 userId: {}", 
                    user.getId(), queue.getId(), queue.getUser().getId());
            throw new BadRequestException("본인이 등록한 대기열만 취소할 수 있습니다.");
        }

        // 대기 상태가 WAITING인지 확인
        if (!WaitingQueueStatus.WAITING.getCode().equals(queue.getStatus())) {
            log.warn("대기 취소 실패 - 대기 상태 아님. queueId: {}, status: {}", queue.getId(), queue.getStatus());
            throw new BadRequestException("대기 중인 항목만 취소할 수 있습니다.");
        }

        // 대기열 상태를 CANCELED로 변경
        queue.setStatus(WaitingQueueStatus.CANCELED);
        waitingQueueRepository.save(queue);

        log.info("대기 취소 완료 - userId: {}, centerId: {}, queueId: {}", 
                user.getId(), centerId, queue.getId());
    }

}
