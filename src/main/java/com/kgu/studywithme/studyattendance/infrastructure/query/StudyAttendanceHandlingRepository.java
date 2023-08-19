package com.kgu.studywithme.studyattendance.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.studyattendance.application.adapter.StudyAttendanceHandlingRepositoryAdapter;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.NonAttendanceWeekly;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.QNonAttendanceWeekly;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.QStudyAttendanceWeekly;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.StudyAttendanceWeekly;
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
                .where(studyAttendance.status.eq(NON_ATTENDANCE))
                .orderBy(
                        studyAttendance.studyId.asc(),
                        studyAttendance.week.asc(),
                        studyAttendance.participantId.asc()
                )
                .fetch();
    }

    @Override
    public List<StudyAttendanceWeekly> findParticipateWeeksInStudyByMemberId(final Long memberId) {
        return query
                .select(
                        new QStudyAttendanceWeekly(
                                studyAttendance.studyId,
                                studyAttendance.week
                        )
                )
                .from(studyAttendance)
                .where(studyAttendance.participantId.eq(memberId))
                .orderBy(studyAttendance.studyId.asc(), studyAttendance.week.asc())
                .fetch();
    }
}