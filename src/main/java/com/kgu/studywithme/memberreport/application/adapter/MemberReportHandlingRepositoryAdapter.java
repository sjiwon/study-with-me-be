package com.kgu.studywithme.memberreport.application.adapter;

public interface MemberReportHandlingRepositoryAdapter {
    boolean isReportStillPending(final Long reporterId, final Long reporteeId);
}
