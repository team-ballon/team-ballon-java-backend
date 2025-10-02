package com.ballon.domain.report.entity;

import com.ballon.domain.report.entity.type.AiReportType;
import com.ballon.domain.report.entity.type.AiReportTypeConverter;
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
    @Column(name = "type", nullable = false)
    @Convert(converter = AiReportTypeConverter.class)
    private AiReportType aiReportType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false)
    private String contentFormat;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "JSONB")
    private String contentJson;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
