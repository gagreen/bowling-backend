package com.gagreen.bowling.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gagreen.bowling.common.dto.ApiBody;
import com.gagreen.bowling.common.dto.ErrorBody;
import com.gagreen.bowling.security.JwtAuthenticationFilter;
import com.gagreen.bowling.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private static final String[] AUTH_WHITELIST = {"/user-api/auth/**", "/staff-api/auth/**",
            "/user-api/centers", "/user-api/centers/*",
            "/v3/api-docs/**", "/swagger-ui/**"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        //@PreAuthorization 사용 -> 모든 경로에 대한 인증처리는 Pass
                        .requestMatchers("/staff-api/**").hasAuthority("STAFF")
                        .anyRequest().authenticated()

                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /* CORS 허용하도록 커스터마이징 진행 */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 인증정보 주고받도록 허용
        config.setAllowCredentials(true);
        //
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        // BCrypt 알고리즘만 사용해서 접두어 없이 순수한 해시값만 저장됨
        return new BCryptPasswordEncoder();
    }

    // 401 에러 처리 (인증 실패)
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            log.warn("인증 실패 - URI: {}, 메시지: {}", request.getRequestURI(), authException.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "인증이 필요합니다.", "유효한 인증 정보가 없습니다.");
        };
    }

    // 403 에러 처리 (권한 부족)
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            log.warn("권한 부족 - URI: {}, 메시지: {}", request.getRequestURI(), accessDeniedException.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                    "접근 권한이 없습니다.", "해당 리소스에 접근할 권한이 없습니다.");
        };
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
