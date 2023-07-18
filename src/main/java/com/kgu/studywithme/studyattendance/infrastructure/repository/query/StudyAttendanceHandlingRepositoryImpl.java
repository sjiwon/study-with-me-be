package com.kgu.studywithme.studyattendance.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.Set;

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

//    @StudyWithMeWritableTransactional
//    @Modifying(flushAutomatically = true, clearAutomatically = true)
//    @Override
//    public void updateParticipantStatus(
//            final Long studyId,
//            final int week,
//            final Set<Long> participantIds,
//            final AttendanceStatus attendanceStatus
//    ) {
//        query
//                .update(studyAttendance)
//                .set(studyAttendance.status, attendanceStatus)
//                .where(
//                        studyIdEq(studyId),
//                        weekEq(week),
//                        participantIdsIn(participantIds)
//                )
//                .execute();
//    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return studyAttendance.studyId.eq(studyId);
    }

    private BooleanExpression participantIdEq(final Long participantId) {
        return studyAttendance.participantId.eq(participantId);
    }

    private BooleanExpression participantIdsIn(final Set<Long> participantIds) {
        if (CollectionUtils.isEmpty(participantIds)) {
            return null;
        }
        return studyAttendance.participantId.in(participantIds);
    }

    private BooleanExpression weekEq(final int week) {
        return studyAttendance.week.eq(week);
    }
}
