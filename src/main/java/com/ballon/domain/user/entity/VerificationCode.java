package com.ballon.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_code",
        indexes = {
                @Index(name = "idx_verification_email_code", columnList = "email, code"),
                @Index(name = "idx_verification_expires_at", columnList = "expiresAt")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean used = false;

    public static VerificationCode of(String email, String code, LocalDateTime expiresAt) {
        return VerificationCode.builder()
                .email(email)
                .code(code)
                .expiresAt(expiresAt)
                .build();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

