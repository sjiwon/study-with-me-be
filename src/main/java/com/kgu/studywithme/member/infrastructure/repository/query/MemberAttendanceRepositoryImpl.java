package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.QStudyParticipateWeeks;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.StudyParticipateWeeks;
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
                .orderBy(studyAttendance.studyId.asc(), studyAttendance.week.asc())
                .fetch();
    }

    private BooleanExpression participantIdEq(final Long memberId) {
        return studyAttendance.participantId.eq(memberId);
    }
}
