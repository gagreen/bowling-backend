package com.gagreen.bowling.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Slf4j
@Component
@Order(1)
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 요청 본문을 캐싱하기 위한 래퍼 사용
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();
        
        try {
            // 요청 로깅
            logRequest(wrappedRequest);
            
            chain.doFilter(wrappedRequest, wrappedResponse);
            
        } finally {
            // 응답 로깅
            long duration = System.currentTimeMillis() - startTime;
            logResponse(wrappedRequest, wrappedResponse, duration);
            
            // 응답 본문을 클라이언트로 복사
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(HttpServletRequest request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== HTTP Request ==========\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");
        logMessage.append("URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Query String: ").append(request.getQueryString() != null ? request.getQueryString() : "").append("\n");
        logMessage.append("Remote Address: ").append(request.getRemoteAddr()).append("\n");
        logMessage.append("Remote Host: ").append(request.getRemoteHost()).append("\n");
        
        // 헤더 정보
        logMessage.append("Headers:\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            // Authorization 헤더는 보안상 일부만 표시
            if ("Authorization".equalsIgnoreCase(headerName) && headerValue != null && headerValue.length() > 20) {
                logMessage.append("  ").append(headerName).append(": ").append(headerValue.substring(0, 20)).append("...\n");
            } else {
                logMessage.append("  ").append(headerName).append(": ").append(headerValue).append("\n");
            }
        }
        
        // 요청 본문
        ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper) request;
        byte[] content = wrappedRequest.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, StandardCharsets.UTF_8);
            // 비밀번호 필드는 마스킹 처리
            body = maskSensitiveData(body);
            logMessage.append("Body: ").append(body).append("\n");
        }
        
        logMessage.append("===================================");
        log.debug(logMessage.toString());
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response, long duration) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== HTTP Response ==========\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");
        logMessage.append("URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Status: ").append(response.getStatus()).append("\n");
        logMessage.append("Duration: ").append(duration).append("ms\n");
        
        // 응답 헤더
        logMessage.append("Headers:\n");
        response.getHeaderNames().forEach(headerName -> {
            logMessage.append("  ").append(headerName).append(": ").append(response.getHeader(headerName)).append("\n");
        });
        
        // 응답 본문
        ContentCachingResponseWrapper wrappedResponse = (ContentCachingResponseWrapper) response;
        byte[] content = wrappedResponse.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, StandardCharsets.UTF_8);
            // 응답 본문이 너무 길면 일부만 표시
            if (body.length() > 1000) {
                logMessage.append("Body: ").append(body.substring(0, 1000)).append("... (truncated)\n");
            } else {
                logMessage.append("Body: ").append(body).append("\n");
            }
        }
        
        logMessage.append("====================================");
        log.debug(logMessage.toString());
    }

    private String maskSensitiveData(String body) {
        // 비밀번호 필드 마스킹
        return body.replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"****\"")
                   .replaceAll("\"password\"\\s*:\\s*'[^']*'", "\"password\":'****'");
    }
}
