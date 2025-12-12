package com.gagreen.bowling.domain.staff;

import com.gagreen.bowling.common.SignInResultDto;
import com.gagreen.bowling.domain.staff.dto.StaffSignUpDto;
import com.gagreen.bowling.domain.user.dto.SignInDto;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterRepository;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.JwtTokenProvider;
import com.gagreen.bowling.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final BowlingCenterRepository bowlingCenterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public SignInResultDto signIn(SignInDto dto) {
        log.info("스태프 로그인 시도 - account: {}", dto.getAccount());
        
        StaffVo staff = staffRepository.findByAccount(dto.getAccount())
                .orElseThrow(() -> {
                    log.warn("스태프 로그인 실패 - 계정 없음: {}", dto.getAccount());
                    return new BadRequestException("계정 또는 비밀번호가 올바르지 않습니다.");
                });

        if (!passwordEncoder.matches(dto.getPassword(), staff.getPassword())) {
            log.warn("스태프 로그인 실패 - 비밀번호 불일치: {}", dto.getAccount());
            throw new BadRequestException("계정 또는 비밀번호가 올바르지 않습니다.");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                staff, null, staff.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        SignInResultDto signInResultDto = jwtTokenProvider.generateToken(authentication);
        
        // Refresh Token을 DB에 저장
        staff.setRefreshToken(signInResultDto.getRefreshToken());
        staffRepository.save(staff);

        log.info("스태프 로그인 성공 - staffId: {}, account: {}", staff.getId(), staff.getAccount());
        return signInResultDto;
    }

    @Transactional
    public SignInResultDto refresh(String refreshToken) {
        log.debug("스태프 토큰 갱신 요청 - refreshToken 길이: {}", refreshToken != null ? refreshToken.length() : 0);
        
        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("스태프 토큰 갱신 실패 - refreshToken이 비어있음");
            throw new BadRequestException("리프레시 토큰이 필요합니다.");
        }
        
        // DB에서 refreshToken과 일치하는 스태프 찾기
        StaffVo staff = staffRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("스태프 토큰 갱신 실패 - 유효하지 않은 refreshToken");
                    return new BadRequestException("유효하지 않은 리프레시 토큰입니다.");
                });
        
        // 새로운 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                staff, null, staff.getAuthorities());
        SignInResultDto newToken = jwtTokenProvider.generateToken(authentication);
        
        // 새로운 refreshToken을 DB에 저장
        staff.setRefreshToken(newToken.getRefreshToken());
        staffRepository.save(staff);
        
        log.info("스태프 토큰 갱신 성공 - staffId: {}", staff.getId());
        return newToken;
    }

    @Transactional
    public StaffVo signUp(StaffSignUpDto dto) {
        log.info("스태프 회원가입 시도 - account: {}, name: {}, centerId: {}", 
                dto.getAccount(), dto.getName(), dto.getCenterId());
        
        if (staffRepository.existsByAccount(dto.getAccount())) {
            log.warn("스태프 회원가입 실패 - 이미 사용 중인 계정: {}", dto.getAccount());
            throw new BadRequestException("이미 사용 중인 계정입니다.");
        }

        BowlingCenterVo center = null;
        if (dto.getCenterId() != null) {
            center = bowlingCenterRepository.findById(dto.getCenterId())
                    .orElseThrow(() -> {
                        log.warn("스태프 회원가입 실패 - 존재하지 않는 센터: {}", dto.getCenterId());
                        return new ResourceNotFoundException("존재하지 않는 센터입니다.");
                    });
        }

        StaffVo staff = new StaffVo();
        staff.setCenter(center);
        staff.setAccount(dto.getAccount());
        staff.setPw(passwordEncoder.encode(dto.getPassword()));
        staff.setName(dto.getName());
        staff.setPhoneNumber(dto.getPhoneNumber());
        Instant now = Instant.now();
        staff.setCreatedAt(now);
        staff.setUpdatedAt(now);

        StaffVo savedStaff = staffRepository.save(staff);
        log.info("스태프 회원가입 성공 - staffId: {}, account: {}", savedStaff.getId(), savedStaff.getAccount());
        return savedStaff;
    }

    @Transactional
    public StaffVo assignCenter(Long centerId) {
        StaffVo staff = getCurrentStaff();
        log.info("스태프 센터 배정 시도 - staffId: {}, centerId: {}", staff.getId(), centerId);

        BowlingCenterVo center = bowlingCenterRepository.findById(centerId)
                .orElseThrow(() -> {
                    log.warn("스태프 센터 배정 실패 - 존재하지 않는 센터: {}", centerId);
                    return new ResourceNotFoundException("존재하지 않는 센터입니다.");
                });

        Long oldCenterId = staff.getCenter() != null ? staff.getCenter().getId() : null;
        staff.setCenter(center);
        staff.setUpdatedAt(Instant.now());
        StaffVo updatedStaff = staffRepository.save(staff);
        log.info("스태프 센터 배정 완료 - staffId: {}, 이전 centerId: {}, 새 centerId: {}", 
                staff.getId(), oldCenterId, centerId);
        return updatedStaff;
    }

    private StaffVo getCurrentStaff() {
        StaffVo staff = SecurityUtil.getCurrentStaff();
        return staffRepository.findById(staff.getId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 스태프입니다."));
    }
}
