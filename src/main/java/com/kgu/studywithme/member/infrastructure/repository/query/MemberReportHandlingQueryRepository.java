package com.kgu.studywithme.member.infrastructure.repository.query;

public interface MemberReportHandlingQueryRepository {
    boolean isReportStillPending(Long reporterId, Long reporteeId);
}
