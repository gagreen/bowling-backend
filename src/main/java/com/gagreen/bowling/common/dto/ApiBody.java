package com.gagreen.bowling.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiBody<T> {
    private Boolean success;
    private Integer statusCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SS", timezone = "Asia/Seoul")
    private Date timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ApiBody<T> ok(T data) {
        return ok(data, new Date());
    }

    public static <T> ApiBody<T> ok(T data, Date timestamp) {
        return ApiBody.<T>builder()
                .success(true)
                .statusCode(200)
                .timestamp(timestamp)
                .data(data)
                .build();
    }

    public static ApiBody<ErrorBody> error(Integer statusCode, Date timestamp, String message, String description) {
        ErrorBody errorBody = new ErrorBody(message, description);

        return ApiBody.<ErrorBody>builder()
                .success(false)
                .timestamp(timestamp)
                .statusCode(statusCode)
                .data(errorBody)
                .build();
    }

    public static <T> ApiBody<T> error(Integer statusCode, Date timestamp, T errors) {


        return ApiBody.<T>builder()
                .success(false)
                .timestamp(timestamp)
                .statusCode(statusCode)
                .data(errors)
                .build();
    }
}
