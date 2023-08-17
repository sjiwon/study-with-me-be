package com.kgu.studywithme.studyweekly.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.infrastructure.query.dto.AutoAttendanceAndFinishedWeekly;
import com.kgu.studywithme.studyweekly.infrastructure.query.dto.QAutoAttendanceAndFinishedWeekly;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
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
    public boolean isLatestWeek(final Long studyId, final Long weeklyId) {
        final List<Long> weeks = query
                .select(studyWeekly.id)
                .from(studyWeekly)
                .where(studyIdEq(studyId))
                .orderBy(studyWeekly.id.desc())
                .fetch();

        if (weeks.isEmpty()) {
            return true;
        }
        return weeks.get(0).equals(weeklyId);
    }

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Override
    public void deleteSpecificWeekly(final Long studyId, final Long weeklyId) {
        final Integer week = query
                .select(studyWeekly.week)
                .from(studyWeekly)
                .where(weeklyIdEq(weeklyId))
                .fetchOne();

        if (week != null) {
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
                    .where(weeklyIdEq(weeklyId))
                    .execute();
        }
    }

    @Override
    public Optional<StudyWeeklySubmit> getSubmittedAssignment(
            final Long memberId,
            final Long studyId,
            final Long weeklyId
    ) {
        return Optional.ofNullable(
                query
                        .selectFrom(studyWeeklySubmit)
                        .innerJoin(studyWeeklySubmit.studyWeekly, studyWeekly)
                        .where(
                                studyWeeklySubmit.participantId.eq(memberId),
                                studyIdEq(studyId),
                                weeklyIdEq(weeklyId)
                        )
                        .fetchOne()
        );
    }

    @Override
    public List<AutoAttendanceAndFinishedWeekly> findAutoAttendanceAndFinishedWeekly() {
        final LocalDateTime now = LocalDateTime.now();

        return query
                .select(
                        new QAutoAttendanceAndFinishedWeekly(
                                studyWeekly.studyId,
                                studyWeekly.week
                        )
                )
                .from(studyWeekly)
                .where(
                        studyWeekly.autoAttendance.isTrue(),
                        studyWeekly.period.endDate.before(now)
                )
                .orderBy(studyWeekly.studyId.asc(), studyWeekly.week.asc())
                .fetch();
    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return studyWeekly.studyId.eq(studyId);
    }

    private BooleanExpression weeklyIdEq(final Long weeklyId) {
        return studyWeekly.id.eq(weeklyId);
    }
}
