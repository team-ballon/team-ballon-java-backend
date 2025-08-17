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

        String title = "[BALA] 이메일 인증 코드";
        String content = """
  <html>
  <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
  <div style="max-width: 600px; margin: auto; background: white; border: 1px solid #ddd; border-radius: 8px; padding: 20px;">
    <h2 style="color: #333; text-align: center;">이메일 인증</h2>
      <p style="font-size: 14px; color: #555; text-align: center;">
      아래 인증 코드를 입력해 주세요:
        </p>
      <div style="background: #f0f0f0; padding: 15px; margin: 20px auto; text-align: center; font-size: 24px; font-weight: bold; color: #2c3e50; border-radius: 6px; width: fit-content;">
        %s
        </div>
      <p style="font-size: 12px; color: #999; text-align: center; margin-top: 30px;">
      본 메일은 발신 전용입니다. 문의사항은 홈페이지를 통해 문의해주세요.
        </p>
      </div>
    </body>
  </html>
  """.formatted(code);
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
