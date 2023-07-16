package com.kgu.studywithme.member.infrastructure.repository.query;

public interface MemberReportHandlingRepository {
    boolean isReportStillPending(Long reporterId, Long reporteeId);
}
