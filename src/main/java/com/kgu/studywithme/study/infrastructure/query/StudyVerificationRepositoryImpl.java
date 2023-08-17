package com.kgu.studywithme.study.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.kgu.studywithme.study.domain.QStudy.study;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyVerificationRepositoryImpl implements StudyVerificationRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean isHost(final Long studyId, final Long memberId) {
        return query
                .select(study.hostId)
                .from(study)
                .where(study.id.eq(studyId))
                .fetchOne().equals(memberId);
    }
}
