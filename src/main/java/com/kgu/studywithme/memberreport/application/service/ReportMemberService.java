package com.kgu.studywithme.memberreport.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreport.application.adapter.MemberReportHandlingRepositoryAdapter;
import com.kgu.studywithme.memberreport.application.usecase.command.ReportMemberUseCase;
import com.kgu.studywithme.memberreport.domain.MemberReport;
import com.kgu.studywithme.memberreport.domain.MemberReportRepository;
import com.kgu.studywithme.memberreport.exception.MemberReportErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportMemberService implements ReportMemberUseCase {
    private final MemberReportHandlingRepositoryAdapter memberReportHandlingRepositoryAdapter;
    private final MemberReportRepository memberReportRepository;

    @Override
    public Long invoke(final Command command) {
        validatePreviousReportIsStillPending(command.reporterId(), command.reporteeId());

        final MemberReport report = MemberReport.createReportWithReason(command.reporterId(), command.reporteeId(), command.reason());
        return memberReportRepository.save(report).getId();
    }

    private void validatePreviousReportIsStillPending(
            final Long reporterId,
            final Long reporteeId
    ) {
        if (memberReportHandlingRepositoryAdapter.isReportStillPending(reporterId, reporteeId)) {
            throw StudyWithMeException.type(MemberReportErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING);
        }
    }
}
