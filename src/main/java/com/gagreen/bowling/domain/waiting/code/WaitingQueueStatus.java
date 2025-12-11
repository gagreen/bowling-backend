package com.gagreen.bowling.domain.waiting.code;

import lombok.Getter;

@Getter
public enum WaitingQueueStatus {
    WAITING("WAITING", "대기중"),
    CANCELED("CANCELED", "취소"),
    DONE("DONE", "배정완료");

    private final String code;
    private final String description;

    WaitingQueueStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static WaitingQueueStatus fromCode(String code) {
        for (WaitingQueueStatus status : WaitingQueueStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

}
