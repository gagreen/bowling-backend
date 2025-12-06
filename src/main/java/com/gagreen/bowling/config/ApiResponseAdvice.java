package com.gagreen.bowling.config;

import com.gagreen.bowling.common.dto.ApiBody;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

import java.util.Date;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // 모든 응답을 가로챈다
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // 이미 ApiResponse 형태면 그대로 반환 (중복 래핑 방지)
        if (body instanceof ApiBody) {
            return body;
        }

        // String 타입은 특별 처리 필요
        if (body instanceof String) {
            return new ObjectMapper().writeValueAsString(ApiBody.ok(body));
        }

        return ApiBody.ok(body, new Date());
    }
}
