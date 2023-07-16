package com.kgu.studywithme.report.domain;

import com.kgu.studywithme.report.infrastructure.repository.query.MemberReportHandlingRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends
        JpaRepository<Report, Long>,
        MemberReportHandlingRepository {
}
