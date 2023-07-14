package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.usecase.command.MemberReportUseCase;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.domain.report.ReportRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class MemberReportService implements MemberReportUseCase {
    private final MemberRepository memberRepository;
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
        if (memberRepository.isReportReceived(reporterId, reporteeId)) {
            throw StudyWithMeException.type(MemberErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING);
        }
    }
}
