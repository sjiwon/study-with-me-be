package com.kgu.studywithme.report.infrastructure.repository.query;

public interface MemberReportHandlingRepository {
    boolean isReportStillPending(Long reporterId, Long reporteeId);
}
