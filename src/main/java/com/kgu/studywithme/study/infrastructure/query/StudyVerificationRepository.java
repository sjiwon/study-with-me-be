package com.kgu.studywithme.study.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.study.application.adapter.StudyVerificationRepositoryAdapter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.kgu.studywithme.study.domain.model.QStudy.study;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyVerificationRepository implements StudyVerificationRepositoryAdapter {
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
