package com.kgu.studywithme.favorite.application.usecase.command;

import com.kgu.studywithme.favorite.domain.model.Favorite;

public record MarkStudyLikeCommand(
        Long memberId,
        Long studyId
) {
    public Favorite toDomain() {
        return Favorite.favoriteMarking(memberId, studyId);
    }
}
