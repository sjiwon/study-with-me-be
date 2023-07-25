package com.kgu.studywithme.memberreport.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreport.application.usecase.command.ReportMemberUseCase;
import com.kgu.studywithme.memberreport.domain.MemberReport;
import com.kgu.studywithme.memberreport.domain.MemberReportRepository;
import com.kgu.studywithme.memberreport.exception.MemberReportErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class ReportMemberService implements ReportMemberUseCase {
    private final MemberReportRepository memberReportRepository;

    @Override
    public Long report(final Command command) {
        validatePreviousReportIsStillPending(command.reporterId(), command.reporteeId());

        final MemberReport report
                = MemberReport.createReportWithReason(command.reporterId(), command.reporteeId(), command.reason());
        return memberReportRepository.save(report).getId();
    }

    private void validatePreviousReportIsStillPending(
            final Long reporterId,
            final Long reporteeId
    ) {
        if (memberReportRepository.isReportStillPending(reporterId, reporteeId)) {
            throw StudyWithMeException.type(MemberReportErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING);
        }
    }
}
