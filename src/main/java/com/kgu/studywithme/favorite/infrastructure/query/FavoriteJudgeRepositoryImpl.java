package com.kgu.studywithme.favorite.infrastructure.query;

import com.kgu.studywithme.favorite.application.adapter.FavoriteJudgeRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;

@Repository
@RequiredArgsConstructor
public class FavoriteJudgeRepositoryImpl implements FavoriteJudgeRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean alreadyLikeMarked(final Long studyId, final Long memberId) {
        return query
                .select(favorite.count())
                .from(favorite)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId)
                )
                .fetchOne() > 0;
    }

    @Override
    public boolean neverLikeMarked(final Long studyId, final Long memberId) {
        return query
                .select(favorite.count())
                .from(favorite)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId)
                )
                .fetchOne() == 0;
    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return favorite.studyId.eq(studyId);
    }

    private BooleanExpression memberIdEq(final Long memberId) {
        return favorite.memberId.eq(memberId);
    }
}
