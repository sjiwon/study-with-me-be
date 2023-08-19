package com.kgu.studywithme.memberreport.application.adapter;

public interface MemberReportHandlingRepositoryAdapter {
    boolean isReportStillPending(Long reporterId, Long reporteeId);
}
