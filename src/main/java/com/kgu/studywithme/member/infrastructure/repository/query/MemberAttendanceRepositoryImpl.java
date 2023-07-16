package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.QAttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.QStudyParticipateWeeks;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyParticipateWeeks;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kgu.studywithme.study.domain.attendance.QAttendance.attendance;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberAttendanceRepositoryImpl implements MemberAttendanceRepository {
    private final JPAQueryFactory query;

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
        return AttendanceStatus.getAttendanceStatuses()
                .stream()
                .map(status -> fetchResult.stream()
                        .filter(ratio -> ratio.status() == status)
                        .findFirst()
                        .orElse(new AttendanceRatio(status, 0))
                ).toList();
    }

    @Override
    public List<StudyParticipateWeeks> findParticipateWeeksInStudyByMemberId(final Long memberId) {
        return query
                .select(
                        new QStudyParticipateWeeks(
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

    private BooleanExpression participantIdEq(final Long memberId) {
        return (memberId != null) ? attendance.participant.id.eq(memberId) : null;
    }
}
