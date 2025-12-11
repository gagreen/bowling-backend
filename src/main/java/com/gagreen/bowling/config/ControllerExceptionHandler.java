package com.gagreen.bowling.config;

import com.gagreen.bowling.common.dto.ApiBody;
import com.gagreen.bowling.common.dto.ErrorBody;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiBody<ErrorBody>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.warn("ResourceNotFoundException 발생 - URI: {}, 메시지: {}", 
                request.getDescription(false), ex.getMessage(), ex);
        
        ApiBody<ErrorBody> body = ApiBody.error(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiBody<ErrorBody>> handleResourceBadRequestException(BadRequestException ex, WebRequest request) {
        log.warn("BadRequestException 발생 - URI: {}, 메시지: {}", 
                request.getDescription(false), ex.getMessage(), ex);
        
        ApiBody<ErrorBody> body = ApiBody.error(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiBody<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.warn("ValidationException 발생 - URI: {}, 오류: {}", 
                request.getDescription(false), errors, ex);

        ApiBody body = ApiBody.error(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                errors
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiBody<ErrorBody>> handleGlobalException(Exception ex, WebRequest request) {
        log.error("예상치 못한 예외 발생 - URI: {}, 예외 타입: {}, 메시지: {}", 
                request.getDescription(false), ex.getClass().getName(), ex.getMessage(), ex);
        
        ApiBody<ErrorBody> body = ApiBody.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
