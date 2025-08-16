package com.ballon.domain.user.service.impl;

import com.ballon.domain.user.entity.VerificationCode;
import com.ballon.domain.user.repository.VerificationCodeRepository;
import com.ballon.domain.user.service.EmailService;
import com.ballon.global.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl {
    private static final long EXPIRATION_MILLIS = 600000;
    private final EmailService emailService;
    private final VerificationCodeRepository repository;

    // 이메일로 인증 코드를 발급 및 전송
    public void sendCodeToEmail(String email) {
        String code = generateRandomCode(6);
        VerificationCode vc = VerificationCode.of(email, code, LocalDateTime.now().plusNanos(EXPIRATION_MILLIS * 1_000_000));
        repository.save(vc);

        String title = "이메일 인증 코드";
        String content = "<h1>인증코드: " + code + "</h1><p>입력하세요.</p>";
        try {
            emailService.sendEmail(email, title, content);
        } catch (Exception e) {
            throw new BadRequestException("이메일 전송 실패");
        }
    }

    // 이메일 + 코드 검증 (만료/사용 여부 확인 후 성공 시 used=true로 업데이트)
    public boolean verifyCode(String email, String code) {
        return repository.findByEmailAndCodeAndUsedFalseAndExpiresAtAfter(email, code, LocalDateTime.now())
                .map(vc -> {
                    vc.setUsed(true);
                    repository.save(vc);
                    return true;
                })
                .orElse(false);
    }

    // 매일 정오에 만료된 인증 코드 삭제
    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional
    public void deleteExpired() {
        repository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return ThreadLocalRandom.current()
                .ints(length, 0, chars.length())
                .mapToObj(chars::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
