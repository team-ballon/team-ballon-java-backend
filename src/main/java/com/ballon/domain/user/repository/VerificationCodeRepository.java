package com.ballon.domain.user.repository;

import com.ballon.domain.user.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByEmailAndCodeAndUsedFalseAndExpiresAtAfter(String email, String code, LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime now);

    boolean existsByEmailAndUsedTrue(String email);
}
