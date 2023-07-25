package com.kgu.studywithme.memberreport.domain;

import com.kgu.studywithme.memberreport.infrastructure.repository.query.MemberReportHandlingRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberReportRepository extends
        JpaRepository<MemberReport, Long>,
        MemberReportHandlingRepository {
}
