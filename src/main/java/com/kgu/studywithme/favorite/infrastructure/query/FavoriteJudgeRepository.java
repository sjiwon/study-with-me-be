package com.kgu.studywithme.favorite.infrastructure.query;

import com.kgu.studywithme.favorite.application.adapter.FavoriteJudgeRepositoryAdapter;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class FavoriteJudgeRepository implements FavoriteJudgeRepositoryAdapter {
    private final JPAQueryFactory query;

    @Override
    public boolean alreadyLikeMarked(final Long memberId, final Long studyId) {
        return query
                .select(favorite.count())
                .from(favorite)
                .where(
                        memberIdEq(memberId),
                        studyIdEq(studyId)
                )
                .fetchOne() > 0;
    }

    @Override
    public boolean neverLikeMarked(final Long memberId, final Long studyId) {
        return query
                .select(favorite.count())
                .from(favorite)
                .where(
                        memberIdEq(memberId),
                        studyIdEq(studyId)
                )
                .fetchOne() == 0;
    }

    private BooleanExpression memberIdEq(final Long memberId) {
        return favorite.memberId.eq(memberId);
    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return favorite.studyId.eq(studyId);
    }
}
