package com.kgu.studywithme.studyweekly.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
                .where(studyWeekly.studyId.eq(studyId))
                .orderBy(studyWeekly.week.desc())
                .fetch();

        if (weeks.isEmpty()) {
            return 1;
        }
        return weeks.get(0) + 1;
    }
}
