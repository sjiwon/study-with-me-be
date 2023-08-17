package com.kgu.studywithme.memberreport.infrastructure.query;

public interface MemberReportHandlingRepository {
    boolean isReportStillPending(Long reporterId, Long reporteeId);
}
