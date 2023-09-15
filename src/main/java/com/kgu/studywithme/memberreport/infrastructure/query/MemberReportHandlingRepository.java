package com.kgu.studywithme.memberreport.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.memberreport.application.adapter.MemberReportHandlingRepositoryAdapter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.RECEIVE;
import static com.kgu.studywithme.memberreport.domain.model.QMemberReport.memberReport;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberReportHandlingRepository implements MemberReportHandlingRepositoryAdapter {
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
                .fetchFirst() == RECEIVE;
    }
}
