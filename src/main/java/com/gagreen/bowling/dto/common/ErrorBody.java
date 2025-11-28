package com.gagreen.bowling.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;


@Getter
@AllArgsConstructor
public class ErrorBody {
    private String message;
    private String description;
}