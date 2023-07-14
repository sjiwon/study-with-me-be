package com.kgu.studywithme.member.application.usecase.command;

public interface MemberReportUseCase {
    Long report(Command command);

    record Command(
            Long reporterId,
            Long reporteeId,
            String reason
    ) {
    }
}
