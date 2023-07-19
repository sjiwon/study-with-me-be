package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.QAttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.QStudyParticipateWeeks;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyParticipateWeeks;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kgu.studywithme.studyattendance.domain.QStudyAttendance.studyAttendance;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberAttendanceRepositoryImpl implements MemberAttendanceRepository {
    private final JPAQueryFactory query;

    @Override
    public List<AttendanceRatio> findAttendanceRatioByMemberId(final Long memberId) {
        List<AttendanceRatio> fetchResult = query
                .select(
                        new QAttendanceRatio(
                                studyAttendance.status,
                                studyAttendance.count().intValue()
                        )
                )
                .from(studyAttendance)
                .where(participantIdEq(memberId))
                .groupBy(studyAttendance.status)
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
                                studyAttendance.studyId,
                                studyAttendance.week
                        )
                )
                .from(studyAttendance)
                .where(participantIdEq(memberId))
                .orderBy(studyAttendance.studyId.asc())
                .fetch();
    }

    private BooleanExpression participantIdEq(final Long memberId) {
        return (memberId != null) ? studyAttendance.participantId.eq(memberId) : null;
    }
}
