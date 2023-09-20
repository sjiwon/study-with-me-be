package com.kgu.studywithme.memberreport.domain.repository;

import com.kgu.studywithme.memberreport.domain.model.MemberReport;
import com.kgu.studywithme.memberreport.domain.model.MemberReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.RECEIVE;

public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {
    boolean existsByReporterIdAndReporteeIdAndStatus(final Long reporterId, final Long reporteeId, final MemberReportStatus status);

    default boolean isPreviousReportStillPending(final Long reporterId, final Long reporteeId) {
        return existsByReporterIdAndReporteeIdAndStatus(reporterId, reporteeId, RECEIVE);
    }
}
