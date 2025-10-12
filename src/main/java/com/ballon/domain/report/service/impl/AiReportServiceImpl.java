package com.ballon.domain.report.service.impl;

import com.ballon.domain.report.dto.AiReportResponse;
import com.ballon.domain.report.entity.AiReport;
import com.ballon.domain.report.entity.type.AiReportType;
import com.ballon.domain.report.repository.AiReportRepository;
import com.ballon.domain.report.service.AiReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class AiReportServiceImpl implements AiReportService {
    private final AiReportRepository aiReportRepository;

    @Override
    public AiReportResponse getAiReportResponseByAiReportType(AiReportType aiReportType) {
        AiReport aiReport = aiReportRepository.findFirstByAiReportTypeOrderByCreatedAtDesc(aiReportType)
                .orElse(null);

        return Objects.isNull(aiReport) ? null : new AiReportResponse(
                aiReport.getAiReportType(),
                aiReport.getTitle(),
                aiReport.getSummary(),
                aiReport.getContentFormat(),
                aiReport.getContent(),
                aiReport.getContentJson(),
                aiReport.getCreatedAt()
        );
    }
}
