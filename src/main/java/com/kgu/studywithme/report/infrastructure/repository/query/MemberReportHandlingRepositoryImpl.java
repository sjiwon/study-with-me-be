package com.kgu.studywithme.report.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.report.domain.ReportStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.kgu.studywithme.report.domain.QReport.report;
import static com.kgu.studywithme.report.domain.ReportStatus.RECEIVE;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberReportHandlingRepositoryImpl implements MemberReportHandlingRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean isReportStillPending(final Long reporterId, final Long reporteeId) {
        ReportStatus status = query
                .select(report.status)
                .from(report)
                .where(
                        report.reporterId.eq(reporterId),
                        report.reporteeId.eq(reporteeId)
                )
                .fetchOne();

        return status == RECEIVE;
    }
}
