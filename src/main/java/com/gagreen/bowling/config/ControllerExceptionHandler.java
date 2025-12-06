package com.gagreen.bowling.config;

import com.gagreen.bowling.common.dto.ApiBody;
import com.gagreen.bowling.common.dto.ErrorBody;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiBody<ErrorBody>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ApiBody<ErrorBody> body = ApiBody.error(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiBody<ErrorBody>> handleGlobalException(Exception ex, WebRequest request) {
        ApiBody<ErrorBody> body = ApiBody.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
