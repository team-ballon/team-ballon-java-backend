package com.ballon.domain.report.entity;

import com.ballon.domain.report.entity.type.AiReportType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AiReport {
    @Id
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AiReportType aiReportType;

    @Column(nullable = false)
    private String title;

    private String summary;

    @Column(nullable = false)
    private String contentFormat;

    private String content;

    private String contentJson;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
