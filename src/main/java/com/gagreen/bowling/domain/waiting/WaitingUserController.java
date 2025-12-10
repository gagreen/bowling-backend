package com.gagreen.bowling.domain.waiting;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterService;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.domain.waiting.dto.WaitingListItem;
import com.gagreen.bowling.domain.waiting.dto.WaitingStatusDto;
import com.gagreen.bowling.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api")
public class WaitingUserController {

    private final WaitingQueueRepository waitingQueueRepository;
    private final BowlingCenterService bowlingCenterService;

    // 볼링장에서 바로 들어갈 있는지 / 앱에서 대기 등록해야 하는지 조회
    @GetMapping("/centers/{centerId}/queues")
    public WaitingStatusDto getCenterQueueStatus(@PathVariable Long centerId) {
        BowlingCenterVo center = bowlingCenterService.getItem(centerId, false);

        List<WaitingListItem> queue = waitingQueueRepository.findByCenter(center);
        boolean shouldWait = !(queue == null || queue.isEmpty());

        return new WaitingStatusDto(queue, shouldWait);
    }


    // 로그인된 사용자의 대기 순번 조회
    @GetMapping("/queues/my")
    public List<WaitingListItem> getMyQueueStatus() {
        UserVo user = SecurityUtil.getCurrentUser();

        // 볼링장의 레인 배정 상태 확인하면 됨
        List<WaitingListItem> queue = waitingQueueRepository.findByUser(user);

        return queue;
    }

    // 사용자 측에서 대기 등록하기
    @PostMapping("/queues")
    public void registerToQueue(@PathVariable Long centerId) {
        // 대기 등록 처리
    }


}
