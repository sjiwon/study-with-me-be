package com.kgu.studywithme.favorite.application.adapter;

public interface FavoriteJudgeRepository {
    boolean alreadyLikeMarked(final Long studyId, final Long memberId);

    boolean neverLikeMarked(final Long studyId, final Long memberId);
}
