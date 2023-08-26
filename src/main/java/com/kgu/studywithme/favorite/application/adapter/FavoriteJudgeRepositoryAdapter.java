package com.kgu.studywithme.favorite.application.adapter;

public interface FavoriteJudgeRepositoryAdapter {
    boolean alreadyLikeMarked(final Long memberId, final Long studyId);

    boolean neverLikeMarked(final Long memberId, final Long studyId);
}
