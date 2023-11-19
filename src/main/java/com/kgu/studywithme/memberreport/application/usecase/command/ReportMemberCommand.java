package com.kgu.studywithme.memberreport.application.usecase.command;

public record ReportMemberCommand(
        Long reporterId,
        Long reporteeId,
        String reason
) {
}
