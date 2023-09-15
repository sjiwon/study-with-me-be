package com.kgu.studywithme.memberreport.domain.repository;

import com.kgu.studywithme.memberreport.domain.model.MemberReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {
}
