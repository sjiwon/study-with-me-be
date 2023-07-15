package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.member.domain.report.ReportStatus;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.QAttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.QStudyAttendanceMetadata;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyAttendanceMetadata;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.member.domain.report.QReport.report;
import static com.kgu.studywithme.member.domain.report.ReportStatus.RECEIVE;
import static com.kgu.studywithme.study.domain.attendance.QAttendance.attendance;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberSimpleQueryRepositoryImpl implements MemberSimpleQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean isReportReceived(
            final Long reporterId,
            final Long reporteeId
    ) {
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

    @Override
    public List<StudyAttendanceMetadata> findStudyAttendanceMetadataByMemberId(final Long memberId) {
        return query
                .select(
                        new QStudyAttendanceMetadata(
                                attendance.study.id,
                                attendance.week
                        )
                )
                .from(attendance)
                .where(participantIdEq(memberId))
                .orderBy(attendance.study.id.asc())
                .fetch();
    }

    @Override
    public Long getAttendanceCount(
            final Long studyId,
            final Long memberId,
            final AttendanceStatus status
    ) {
        return query
                .select(attendance.count())
                .from(attendance)
                .where(
                        attendance.study.id.eq(studyId),
                        participantIdEq(memberId),
                        attendance.status.eq(status)
                )
                .fetchOne();
    }

    @Override
    public List<AttendanceRatio> findAttendanceRatioByMemberId(final Long memberId) {
        List<AttendanceRatio> fetchResult = query
                .select(
                        new QAttendanceRatio(
                                attendance.status,
                                attendance.count().intValue()
                        )
                )
                .from(attendance)
                .where(participantIdEq(memberId))
                .groupBy(attendance.status)
                .fetch();

        return includeMissingAttendanceStatus(fetchResult);
    }

    private List<AttendanceRatio> includeMissingAttendanceStatus(final List<AttendanceRatio> fetchResult) {
        List<AttendanceRatio> result = new ArrayList<>();

        for (AttendanceStatus status : AttendanceStatus.getAttendanceStatuses()) {
            AttendanceRatio specificAttendanceRatio = fetchResult.stream()
                    .filter(ratio -> ratio.status() == status)
                    .findFirst()
                    .orElse(new AttendanceRatio(status, 0));

            result.add(specificAttendanceRatio);
        }

        return result;
    }

    private BooleanExpression participantIdEq(final Long memberId) {
        return attendance.participant.id.eq(memberId);
    }
}
