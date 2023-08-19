package com.kgu.studywithme.memberreport.application.usecase.command;

public interface ReportMemberUseCase {
    Long invoke(final Command command);

    record Command(
            Long reporterId,
            Long reporteeId,
            String reason
    ) {
    }
}
