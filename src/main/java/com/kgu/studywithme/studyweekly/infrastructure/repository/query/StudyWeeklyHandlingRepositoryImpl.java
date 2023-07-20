package com.kgu.studywithme.studyweekly.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.kgu.studywithme.studyweekly.domain.QStudyWeekly.studyWeekly;

@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class StudyWeeklyHandlingRepositoryImpl implements StudyWeeklyHandlingRepository {
    private final JPAQueryFactory query;

    @Override
    public int getNextWeek(final Long studyId) {
        final List<Integer> weeks = query
                .select(studyWeekly.week)
                .from(studyWeekly)
                .where(studyIdEq(studyId))
                .orderBy(studyWeekly.week.desc())
                .fetch();

        if (weeks.isEmpty()) {
            return 1;
        }
        return weeks.get(0) + 1;
    }

    @Override
    public Optional<StudyWeekly> getSpecificWeekly(final Long studyId, final int week) {
        return Optional.ofNullable(
                query
                        .selectFrom(studyWeekly)
                        .where(
                                studyIdEq(studyId),
                                studyWeekly.week.eq(week)
                        )
                        .fetchOne()
        );
    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return studyWeekly.studyId.eq(studyId);
    }
}
