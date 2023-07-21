package com.kgu.studywithme.studyweekly.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.kgu.studywithme.studyattendance.domain.QStudyAttendance.studyAttendance;
import static com.kgu.studywithme.studyweekly.domain.QStudyWeekly.studyWeekly;
import static com.kgu.studywithme.studyweekly.domain.attachment.QStudyWeeklyAttachment.studyWeeklyAttachment;
import static com.kgu.studywithme.studyweekly.domain.submit.QStudyWeeklySubmit.studyWeeklySubmit;

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
                                weekEq(week)
                        )
                        .fetchOne()
        );
    }

    @Override
    public boolean isLatestWeek(final Long studyId, final int week) {
        final List<Integer> weeks = query
                .select(studyWeekly.week)
                .from(studyWeekly)
                .where(studyIdEq(studyId))
                .orderBy(studyWeekly.week.desc())
                .fetch();

        if (weeks.isEmpty()) {
            return true;
        }
        return weeks.get(0) == week;
    }

    @Override
    public void deleteSpecificWeekly(final Long studyId, final int week) {
        final Long weeklyId = query
                .select(studyWeekly.id)
                .from(studyWeekly)
                .where(
                        studyIdEq(studyId),
                        weekEq(week)
                )
                .fetchOne();

        if (weeklyId != null) {
            // 1. 제출한 과제 삭제
            query
                    .delete(studyWeeklySubmit)
                    .where(studyWeeklySubmit.studyWeekly.id.eq(weeklyId))
                    .execute();

            // 2. 해당 주차 첨부파일 삭제
            query
                    .delete(studyWeeklyAttachment)
                    .where(studyWeeklyAttachment.studyWeekly.id.eq(weeklyId))
                    .execute();

            // 3. 해당 주차 출석 정보 삭제
            query
                    .delete(studyAttendance)
                    .where(
                            studyAttendance.studyId.eq(studyId),
                            studyAttendance.week.eq(week)
                    )
                    .execute();

            // 4. 해당 주차 삭제
            query
                    .delete(studyWeekly)
                    .where(studyWeekly.id.eq(weeklyId))
                    .execute();
        }
    }

    @Override
    public Optional<StudyWeeklySubmit> getSubmittedAssignment(
            final Long memberId,
            final Long studyId,
            final int week
    ) {
        return Optional.ofNullable(
                query
                        .selectFrom(studyWeeklySubmit)
                        .innerJoin(studyWeeklySubmit.studyWeekly, studyWeekly)
                        .where(
                                studyWeeklySubmit.participantId.eq(memberId),
                                studyIdEq(studyId),
                                weekEq(week)
                        )
                        .fetchOne()
        );
    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return studyWeekly.studyId.eq(studyId);
    }

    private BooleanExpression weekEq(final int week) {
        return studyWeekly.week.eq(week);
    }
}
