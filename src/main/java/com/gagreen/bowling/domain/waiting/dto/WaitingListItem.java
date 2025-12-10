package com.gagreen.bowling.domain.waiting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaitingListItem {
    private Long queueId;
    private Long centerId;
    private String name;
    private Long userId;
    private Integer peopleCount;
    private String status;
    private Instant createdAt;

    private Integer queueNumber; // 대기 순번

    public WaitingListItem(Long queueId, Long centerId, String name, Long userId, Integer peopleCount, String status, Instant createdAt) {
        this.queueId = queueId;
        this.centerId = centerId;
        this.name = name;
        this.userId = userId;
        this.peopleCount = peopleCount;
        this.status = status;
        this.createdAt = createdAt;
    }
}
