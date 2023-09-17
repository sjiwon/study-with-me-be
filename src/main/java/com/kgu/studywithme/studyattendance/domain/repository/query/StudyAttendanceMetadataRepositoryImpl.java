package com.kgu.studywithme.studyattendance.domain.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.NonAttendanceWeekly;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.QNonAttendanceWeekly;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.QStudyAttendanceWeekly;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.StudyAttendanceWeekly;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.QStudyAttendance.studyAttendance;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyAttendanceMetadataRepositoryImpl implements StudyAttendanceMetadataRepository {
    private final JPAQueryFactory query;

    @Override
    public List<NonAttendanceWeekly> findParticipantNonAttendanceWeekly() {
        return query
                .select(new QNonAttendanceWeekly(
                        studyAttendance.studyId,
                        studyAttendance.week,
                        studyAttendance.participantId
                ))
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
    public List<StudyAttendanceWeekly> findMemberParticipateWeekly(final Long memberId) {
        return query
                .select(new QStudyAttendanceWeekly(
                        studyAttendance.studyId,
                        studyAttendance.week
                ))
                .from(studyAttendance)
                .where(studyAttendance.participantId.eq(memberId))
                .orderBy(studyAttendance.studyId.asc(), studyAttendance.week.asc())
                .fetch();
    }
}
