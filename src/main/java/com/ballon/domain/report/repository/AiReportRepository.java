package com.ballon.domain.report.repository;

import com.ballon.domain.report.entity.AiReport;
import com.ballon.domain.report.entity.type.AiReportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiReportRepository extends JpaRepository<AiReport, AiReportType> {
    Optional<AiReport> findFirstByAiReportTypeOrderByCreatedAtDesc(AiReportType aiReportType);
}
