package com.kgu.studywithme.memberreport.infrastructure.repository.query;

public interface MemberReportHandlingRepository {
    boolean isReportStillPending(Long reporterId, Long reporteeId);
}
