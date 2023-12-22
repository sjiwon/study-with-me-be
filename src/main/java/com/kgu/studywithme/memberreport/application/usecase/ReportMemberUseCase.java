package com.kgu.studywithme.memberreport.application.usecase;

import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.memberreport.application.usecase.command.ReportMemberCommand;
import com.kgu.studywithme.memberreport.domain.model.MemberReport;
import com.kgu.studywithme.memberreport.domain.repository.MemberReportRepository;
import com.kgu.studywithme.memberreport.exception.MemberReportErrorCode;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ReportMemberUseCase {
    private final MemberRepository memberRepository;
    private final MemberReportRepository memberReportRepository;

    public Long invoke(final ReportMemberCommand command) {
        final Member reporter = memberRepository.getById(command.reporterId());
        final Member reportee = memberRepository.getById(command.reporteeId());

        validatePreviousReportStillPending(reporter, reportee);
        return memberReportRepository.save(MemberReport.createReport(reporter, reportee, command.reason())).getId();
    }

    private void validatePreviousReportStillPending(final Member reporter, final Member reportee) {
        if (memberReportRepository.isPreviousReportStillPending(reporter.getId(), reportee.getId())) {
            throw StudyWithMeException.type(MemberReportErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING);
        }
    }
}
