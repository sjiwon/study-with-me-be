package com.kgu.studywithme.memberreport.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.kgu.studywithme.memberreport.domain.MemberReportStatus.RECEIVE;
import static com.kgu.studywithme.memberreport.domain.QMemberReport.memberReport;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberReportHandlingRepositoryImpl implements MemberReportHandlingRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean isReportStillPending(final Long reporterId, final Long reporteeId) {
        return query
                .select(memberReport.status)
                .from(memberReport)
                .where(
                        memberReport.reporterId.eq(reporterId),
                        memberReport.reporteeId.eq(reporteeId)
                )
                .fetchOne() == RECEIVE;
    }
}
