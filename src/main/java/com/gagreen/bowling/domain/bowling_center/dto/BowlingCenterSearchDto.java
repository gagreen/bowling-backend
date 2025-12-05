package com.gagreen.bowling.domain.bowling_center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BowlingCenterSearchDto {
    private String keyword;
    private String type;

    private String name;
    private String state;
    private String city;
    private String district;

    public String getType() {
        switch (type) {
            case "name", "state", "city", "district":
                break;
            default:
                type = null;
        }

        return type;
    }
}
