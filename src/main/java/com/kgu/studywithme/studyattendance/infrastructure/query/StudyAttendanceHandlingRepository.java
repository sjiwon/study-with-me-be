package com.kgu.studywithme.studyattendance.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.studyattendance.application.adapter.StudyAttendanceHandlingRepositoryAdapter;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.NonAttendanceWeekly;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.QNonAttendanceWeekly;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.QStudyAttendance.studyAttendance;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyAttendanceHandlingRepository implements StudyAttendanceHandlingRepositoryAdapter {
    private final JPAQueryFactory query;

    @Override
    public List<NonAttendanceWeekly> findNonAttendanceInformation() {
        return query
                .select(
                        new QNonAttendanceWeekly(
                                studyAttendance.studyId,
                                studyAttendance.week,
                                studyAttendance.participantId
                        )
                )
                .from(studyAttendance)
                .where(statusEq(NON_ATTENDANCE))
                .orderBy(
                        studyAttendance.studyId.asc(),
                        studyAttendance.week.asc(),
                        studyAttendance.participantId.asc()
                )
                .fetch();
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

    private BooleanExpression statusEq(final AttendanceStatus status) {
        return studyAttendance.status.eq(status);
    }
}
