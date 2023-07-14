package com.kgu.studywithme.member.application;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.domain.report.ReportRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;

    @StudyWithMeWritableTransactional
    public Long report(
            final Long reporteeId,
            final Long reporterId,
            final String reason
    ) {
        validatePreviousReportIsStillPending(reporteeId, reporterId);

        final Report report = Report.createReportWithReason(reporteeId, reporterId, reason);
        return reportRepository.save(report).getId();
    }

    private void validatePreviousReportIsStillPending(
            final Long reporteeId,
            final Long reporterId
    ) {
        if (memberRepository.isReportReceived(reporteeId, reporterId)) {
            throw StudyWithMeException.type(MemberErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING);
        }
    }
}
