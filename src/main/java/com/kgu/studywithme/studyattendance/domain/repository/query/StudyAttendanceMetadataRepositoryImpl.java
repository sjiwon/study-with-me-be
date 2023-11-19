package com.kgu.studywithme.studyattendance.domain.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.QStudyAttendanceWeekly;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.StudyAttendanceWeekly;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kgu.studywithme.studyattendance.domain.model.QStudyAttendance.studyAttendance;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyAttendanceMetadataRepositoryImpl implements StudyAttendanceMetadataRepository {
    private final JPAQueryFactory query;

    @Override
    public List<StudyAttendanceWeekly> findMemberParticipateWeekly(final Long memberId) {
        return query
                .select(new QStudyAttendanceWeekly(
                        studyAttendance.study.id,
                        studyAttendance.week
                ))
                .from(studyAttendance)
                .where(studyAttendance.participant.id.eq(memberId))
                .orderBy(studyAttendance.study.id.asc(), studyAttendance.week.asc())
                .fetch();
    }
}
