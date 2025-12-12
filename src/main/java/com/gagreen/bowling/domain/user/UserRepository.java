package com.gagreen.bowling.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserVo, Long> {
    boolean existsByAccount(String account);

    Optional<UserVo> findByAccount(String account);
    
    Optional<UserVo> findByRefreshToken(String refreshToken);
}
