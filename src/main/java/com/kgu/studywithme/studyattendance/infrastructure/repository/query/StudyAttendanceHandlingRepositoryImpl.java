package com.kgu.studywithme.studyattendance.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.QStudyAttendance.studyAttendance;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyAttendanceHandlingRepositoryImpl implements StudyAttendanceHandlingRepository {
    private final JPAQueryFactory query;

    @Override
    public Optional<StudyAttendance> getParticipantAttendanceByWeek(
            final Long studyId,
            final Long participantId,
            final Integer week
    ) {
        return Optional.ofNullable(
                query
                        .selectFrom(studyAttendance)
                        .where(
                                studyIdEq(studyId),
                                participantIdEq(participantId),
                                weekEq(week)
                        )
                        .fetchOne()
        );
    }

    @Override
    public int getAttendanceCount(final Long studyId, final Long participantId) {
        return query
                .select(studyAttendance.count())
                .from(studyAttendance)
                .where(
                        studyIdEq(studyId),
                        participantIdEq(participantId),
                        studyAttendance.status.eq(ATTENDANCE)
                )
                .fetchOne()
                .intValue();
    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return studyAttendance.studyId.eq(studyId);
    }

    private BooleanExpression participantIdEq(final Long participantId) {
        return studyAttendance.participantId.eq(participantId);
    }

    private BooleanExpression weekEq(final int week) {
        return studyAttendance.week.eq(week);
    }
}
