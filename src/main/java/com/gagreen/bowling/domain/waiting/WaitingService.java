package com.gagreen.bowling.domain.waiting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WaitingService {
    private final WaitingQueueRepository waitingQueueRepository;

    // 사용자 측에서 대기 등록하기
    @Transactional
    public void registerWaiting(Long userId, Long centerId) {
        // 대기 등록 로직 구현
//        WaitingQueueVo


    }

    // 대기 현황 조회
    @Transactional
    public void getWaitingStatus(Long centerId) {
        // 대기 현황 조회 로직 구현


    }


}
