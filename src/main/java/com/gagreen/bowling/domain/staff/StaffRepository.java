package com.gagreen.bowling.domain.staff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<StaffVo, Integer> {
    Optional<StaffVo> findByAccount(String account);
    boolean existsByAccount(String account);
    
    Optional<StaffVo> findByRefreshToken(String refreshToken);
}
