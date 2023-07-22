package com.kgu.studywithme.study.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.kgu.studywithme.study.domain.QStudy.study;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyDuplicateCheckRepositoryImpl implements StudyDuplicateCheckRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean isNameExists(final String name) {
        return query
                .select(study.id)
                .from(study)
                .where(nameEq(name))
                .fetchFirst() != null;
    }

    @Override
    public boolean isNameUsedByOther(final Long studyId, final String name) {
        return query
                .select(study.id)
                .from(study)
                .where(
                        study.id.ne(studyId),
                        nameEq(name)
                )
                .fetchFirst() != null;
    }

    private BooleanExpression nameEq(final String name) {
        return study.name.value.eq(name);
    }
}