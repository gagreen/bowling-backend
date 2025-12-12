package com.gagreen.bowling.domain.user;

import com.gagreen.bowling.common.SignInResultDto;
import com.gagreen.bowling.domain.user.dto.SignUpDto;
import com.gagreen.bowling.domain.user.dto.UserDto;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserVo getItem(Long userId) {
        log.debug("사용자 조회 시작 - userId: {}", userId);
        UserVo user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));
        log.debug("사용자 조회 완료 - userId: {}, username: {}", userId, user.getUsername());
        return user;
    }

    @Transactional
    public SignInResultDto signIn(String username, String password) {
        log.info("로그인 시도 - username: {}", username);
        
        // 1. username + password 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.debug("인증 성공 - username: {}, 권한: {}", username, authentication.getAuthorities());

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        SignInResultDto signInResultDto = jwtTokenProvider.generateToken(authentication);
        
        // 4. Refresh Token을 DB에 저장
        UserVo user = userRepository.findByAccount(username)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
        user.setRefreshToken(signInResultDto.getRefreshToken());
        userRepository.save(user);

        signInResultDto.setId(user.getId());
        signInResultDto.setNickname(user.getNickname());
        log.info("로그인 성공 - username: {}", username);

        return signInResultDto;
    }

    @Transactional
    public SignInResultDto refresh(String refreshToken) {
        log.debug("토큰 갱신 요청 - refreshToken 길이: {}", refreshToken != null ? refreshToken.length() : 0);
        
        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("토큰 갱신 실패 - refreshToken이 비어있음");
            throw new BadRequestException("리프레시 토큰이 필요합니다.");
        }
        
        // DB에서 refreshToken과 일치하는 사용자 찾기
        UserVo user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("토큰 갱신 실패 - 유효하지 않은 refreshToken");
                    return new BadRequestException("유효하지 않은 리프레시 토큰입니다.");
                });
        
        // 새로운 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SignInResultDto newToken = jwtTokenProvider.generateToken(authentication);
        
        // 새로운 refreshToken을 DB에 저장
        user.setRefreshToken(newToken.getRefreshToken());
        userRepository.save(user);
        
        log.info("토큰 갱신 성공 - userId: {}", user.getId());
        return newToken;
    }

    @Transactional
    public UserDto signUp(SignUpDto dto) {
        log.info("회원가입 시도 - account: {}, name: {}", dto.getAccount(), dto.getName());
        
        if (userRepository.existsByAccount(dto.getAccount())) {
            log.warn("회원가입 실패 - 이미 사용 중인 계정: {}", dto.getAccount());
            throw new BadRequestException("이미 사용 중인 사용자 이름입니다.");
        }
        
        // Password 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        List<String> roles = new ArrayList<>();
        roles.add("USER");  // USER 권한 부여

        UserVo userVo = dto.toEntity(encodedPassword, new Date());

        userRepository.save(userVo);
        log.info("회원가입 성공 - userId: {}, account: {}", userVo.getId(), userVo.getAccount());

        return UserDto.toDto(userVo);
    }
}
