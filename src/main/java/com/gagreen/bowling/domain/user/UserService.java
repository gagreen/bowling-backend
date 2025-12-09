package com.gagreen.bowling.domain.user;

import com.gagreen.bowling.common.JwtToken;
import com.gagreen.bowling.domain.user.dto.SignUpDto;
import com.gagreen.bowling.domain.user.dto.UserDto;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserVo getItem(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));
    }

    @Transactional
    public JwtToken signIn(String username, String password) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }

    @Transactional
    public UserDto signUp(SignUpDto dto) {
        if (userRepository.existsByAccount(dto.getAccount())) {
            throw new BadRequestException("이미 사용 중인 사용자 이름입니다.");
        }
        // Password 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        List<String> roles = new ArrayList<>();
        roles.add("USER");  // USER 권한 부여

        UserVo userVo = dto.toEntity(encodedPassword, new Date());

        userRepository.save(userVo);

        return UserDto.toDto(userVo);
    }
}
