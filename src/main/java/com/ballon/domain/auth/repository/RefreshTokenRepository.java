package com.ballon.domain.auth.repository;

import com.ballon.domain.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}