package com.kgu.studywithme.member.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.member.application.adapter.MemberAttendanceRepositoryAdapter;
import com.kgu.studywithme.member.infrastructure.query.dto.QStudyParticipateWeeks;
import com.kgu.studywithme.member.infrastructure.query.dto.StudyParticipateWeeks;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kgu.studywithme.studyattendance.domain.QStudyAttendance.studyAttendance;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberAttendanceRepository implements MemberAttendanceRepositoryAdapter {
    private final JPAQueryFactory query;

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
                .where(studyAttendance.participantId.eq(memberId))
                .orderBy(studyAttendance.studyId.asc(), studyAttendance.week.asc())
                .fetch();
    }
}
