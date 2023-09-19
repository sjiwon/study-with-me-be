package com.kgu.studywithme.studyweekly.domain.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.studyweekly.domain.repository.query.dto.AutoAttendanceAndFinishedWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.query.dto.QAutoAttendanceAndFinishedWeekly;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.studyweekly.domain.model.QStudyWeekly.studyWeekly;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyWeeklyMetadataRepositoryImpl implements StudyWeeklyMetadataRepository {
    private final JPAQueryFactory query;

    @Override
    public List<AutoAttendanceAndFinishedWeekly> findAutoAttendanceAndFinishedWeekly() {
        final LocalDateTime now = LocalDateTime.now();

        return query
                .select(new QAutoAttendanceAndFinishedWeekly(
                        studyWeekly.studyId,
                        studyWeekly.week
                ))
                .from(studyWeekly)
                .where(
                        studyWeekly.autoAttendance.isTrue(),
                        studyWeekly.period.endDate.before(now)
                )
                .orderBy(studyWeekly.studyId.asc(), studyWeekly.week.asc())
                .fetch();
    }
}
