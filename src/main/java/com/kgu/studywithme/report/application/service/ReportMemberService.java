package com.kgu.studywithme.report.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.report.application.usecase.command.ReportMemberUseCase;
import com.kgu.studywithme.report.domain.Report;
import com.kgu.studywithme.report.domain.ReportRepository;
import com.kgu.studywithme.report.exception.ReportErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class ReportMemberService implements ReportMemberUseCase {
    private final ReportRepository reportRepository;

    @Override
    public Long report(final Command command) {
        validatePreviousReportIsStillPending(command.reporterId(), command.reporteeId());

        final Report report = Report.createReportWithReason(command.reporterId(), command.reporteeId(), command.reason());
        return reportRepository.save(report).getId();
    }

    private void validatePreviousReportIsStillPending(
            final Long reporterId,
            final Long reporteeId
    ) {
        if (reportRepository.isReportStillPending(reporterId, reporteeId)) {
            throw StudyWithMeException.type(ReportErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING);
        }
    }
}
