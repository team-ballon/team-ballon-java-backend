package com.ballon.domain.report.dto;

import com.ballon.domain.report.entity.type.AiReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AiReportResponse {
    private AiReportType aiReportType;
    private String title;
    private String summary;
    private String contentFormat;
    private String content;
    private String contentJson;
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "AiReportResponse{" +
                "aiReportType=" + aiReportType +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
