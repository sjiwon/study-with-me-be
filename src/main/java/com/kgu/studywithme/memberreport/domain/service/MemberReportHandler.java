package com.kgu.studywithme.memberreport.domain.service;

import com.kgu.studywithme.memberreport.domain.repository.MemberReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberReportHandler {
    private final MemberReportRepository memberReportRepository;

    public boolean isPreviousReportStillPending(final Long reporterId, final Long reporteeId) {
        return memberReportRepository.existsByReporterIdAndReporteeId(reporterId, reporteeId);
    }
}
