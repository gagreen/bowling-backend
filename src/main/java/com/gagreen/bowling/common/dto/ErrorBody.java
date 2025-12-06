package com.gagreen.bowling.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ErrorBody {
    private String message;
    private String description;
}