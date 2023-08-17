package com.kgu.studywithme.memberreport.infrastructure.persistence;

import com.kgu.studywithme.memberreport.domain.MemberReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberReportJpaRepository extends JpaRepository<MemberReport, Long> {
}
