package com.gagreen.bowling.security;

import com.gagreen.bowling.common.SignInResultDto;
import com.gagreen.bowling.domain.staff.StaffRepository;
import com.gagreen.bowling.domain.staff.StaffVo;
import com.gagreen.bowling.domain.user.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// ref. https://suddiyo.tistory.com/entry/Spring-Spring-Security-JWT-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-2

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    @Value("${jwt.access-token.expiration_time}")
    private int accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration_time}")
    private int refreshTokenExpiration;

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            UserRepository userRepository,
                            StaffRepository staffRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public SignInResultDto generateToken(Authentication authentication) {
        log.debug("JWT 토큰 생성 시작 - 사용자: {}, 권한: {}", 
                authentication.getName(), authentication.getAuthorities());
        
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        String userType = resolveUserType(authentication.getPrincipal(), authorities);
        log.debug("사용자 타입 결정 - userType: {}", userType);

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenExpiration);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .claim("userType", userType)
                .claim("tokenType", TOKEN_TYPE_ACCESS)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성 (단순한 랜덤 문자열)
        String refreshToken = generateRefreshToken();

        SignInResultDto signInResultDto = SignInResultDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        
        log.debug("JWT 토큰 생성 완료 - 사용자: {}, accessToken 만료: {}, refreshToken 만료: {}", 
                authentication.getName(), accessTokenExpiresIn, new Date(now + refreshTokenExpiration));
        
        return signInResultDto;
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        log.debug("JWT 토큰 인증 정보 추출 시작");
        
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            log.error("JWT 토큰에 권한 정보가 없음 - subject: {}", claims.getSubject());
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        String tokenType = claims.get("tokenType", String.class);
        if (!TOKEN_TYPE_ACCESS.equalsIgnoreCase(tokenType)) {
            log.warn("Access 토큰이 아님 - tokenType: {}, subject: {}", tokenType, claims.getSubject());
            throw new RuntimeException("Access 토큰이 아닙니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        String userType = claims.get("userType", String.class);

        if ("STAFF".equalsIgnoreCase(userType)) {
            log.debug("스태프 인증 정보 생성 - account: {}", claims.getSubject());
            UserDetails principal = staffRepository.findByAccount(claims.getSubject())
                    .orElseThrow(() -> {
                        log.error("스태프를 찾을 수 없음 - account: {}", claims.getSubject());
                        return new RuntimeException("사용자를 찾을 수 없습니다.");
                    });
            return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        }

        // 기본값: USER
        log.debug("사용자 인증 정보 생성 - account: {}", claims.getSubject());
        UserDetails principal = userRepository.findByAccount(claims.getSubject())
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없음 - account: {}", claims.getSubject());
                    return new RuntimeException("사용자를 찾을 수 없습니다.");
                });
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT Token - 토큰 형식이 올바르지 않습니다", e);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token - 토큰이 만료되었습니다. 만료 시간: {}", e.getClaims().getExpiration(), e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token - 지원하지 않는 JWT 토큰입니다", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty - JWT 클레임이 비어있습니다", e);
        }
        return false;
    }


    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String generateRefreshToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String resolveUserType(Object principal, String authorities) {
        if (principal instanceof StaffVo || authorities.contains("STAFF")) {
            return "STAFF";
        }
        return "USER";
    }

}