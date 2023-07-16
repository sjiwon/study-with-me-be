package com.kgu.studywithme.report.application.usecase.command;

public interface ReportMemberUseCase {
    Long report(Command command);

    record Command(
            Long reporterId,
            Long reporteeId,
            String reason
    ) {
    }
}
