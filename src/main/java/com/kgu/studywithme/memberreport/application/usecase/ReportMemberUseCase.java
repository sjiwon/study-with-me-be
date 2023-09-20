package com.kgu.studywithme.memberreport.application.usecase;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreport.application.usecase.command.ReportMemberCommand;
import com.kgu.studywithme.memberreport.domain.repository.MemberReportRepository;
import com.kgu.studywithme.memberreport.exception.MemberReportErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportMemberUseCase {
    private final MemberReportRepository memberReportRepository;

    public Long invoke(final ReportMemberCommand command) {
        if (memberReportRepository.isPreviousReportStillPending(command.reporterId(), command.reporteeId())) {
            throw StudyWithMeException.type(MemberReportErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING);
        }

        return memberReportRepository.save(command.toDomain()).getId();
    }
}
