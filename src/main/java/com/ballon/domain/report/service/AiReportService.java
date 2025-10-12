package com.ballon.domain.report.service;

import com.ballon.domain.report.dto.AiReportResponse;
import com.ballon.domain.report.entity.type.AiReportType;

public interface AiReportService {
    AiReportResponse getAiReportResponseByAiReportType(AiReportType aiReportType);
}
