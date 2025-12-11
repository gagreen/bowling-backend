package com.gagreen.bowling.domain.lane.code;

import com.gagreen.bowling.domain.waiting.code.WaitingQueueStatus;
import lombok.Getter;

@Getter
public enum LaneStatus {
    NORMAL("NORMAL", "정상"),
    ERROR("ERROR", "고장"),
    CLOSED("CLOSED", "마감");

    private final String code;
    private final String description;

    LaneStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static LaneStatus fromCode(String code) {
        for (LaneStatus status : LaneStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
