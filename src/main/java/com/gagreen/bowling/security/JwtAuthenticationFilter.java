package com.gagreen.bowling.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gagreen.bowling.common.dto.ApiBody;
import com.gagreen.bowling.common.dto.ErrorBody;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;

// 클라이언트 요청시 JWT 인증을 하기 위해 설치하는 커스텀 필터
// UsernamePasswordAuthenticationFilter 이전에 실행
// 클라이언트에서 들어오는 요청에서 JWT 토큰 처리
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Request Header에서 JWT 토큰 추출
        String accessToken = resolveToken(httpRequest);

        // accessToken 유효성 검사하기
        if (accessToken != null) {
            log.debug("JWT 토큰 추출 성공 - URI: {}, 토큰 길이: {}", 
                    httpRequest.getRequestURI(), accessToken.length());
            
            if (jwtTokenProvider.validateToken(accessToken)) {
                // 토큰이 유효할 경우, 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장함
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken); // 토큰에 있는 정보 꺼내기
                SecurityContextHolder.getContext().setAuthentication(authentication); // 현재 실행 중인 스레드에 인증 정보를 저장
                log.debug("JWT 인증 성공 - URI: {}, 사용자: {}, 권한: {}", 
                        httpRequest.getRequestURI(), 
                        authentication.getName(), 
                        authentication.getAuthorities());
            } else {
                // 토큰이 유효하지 않은 경우, 더 이상의 필터 처리 하지 않음
                log.warn("JWT 토큰 검증 실패 - URI: {}", httpRequest.getRequestURI());
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, 
                        "인증에 실패했습니다.", "유효하지 않은 토큰입니다.");
                return;
            }
        } else {
            log.debug("JWT 토큰 없음 - URI: {}", httpRequest.getRequestURI());
        }
        chain.doFilter(request, response); // 다음 필터로 요청 전달
    }

    // Request Header에서 JWT 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7); // "Bearer " 이후만 넘기기
        }
        return null;
    }

    // 에러 응답을 JSON 형식으로 반환
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message, String description) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiBody<ErrorBody> errorBody = ApiBody.error(
                statusCode,
                new Date(),
                message,
                description
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), errorBody);
    }
}