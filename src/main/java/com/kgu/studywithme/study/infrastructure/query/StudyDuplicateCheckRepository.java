package com.kgu.studywithme.study.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.study.application.adapter.StudyDuplicateCheckRepositoryAdapter;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.kgu.studywithme.study.domain.QStudy.study;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyDuplicateCheckRepository implements StudyDuplicateCheckRepositoryAdapter {
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
