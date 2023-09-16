package com.kgu.studywithme.memberreport.application.usecase.command;

import com.kgu.studywithme.memberreport.domain.model.MemberReport;

public record ReportMemberCommand(
        Long reporterId,
        Long reporteeId,
        String reason
) {
    public MemberReport toDomain() {
        return MemberReport.createReportWithReason(reporterId, reporteeId, reason);
    }
}
