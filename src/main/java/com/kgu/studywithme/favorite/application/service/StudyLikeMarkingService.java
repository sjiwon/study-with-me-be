package com.kgu.studywithme.favorite.application.service;

import com.kgu.studywithme.favorite.application.adapter.FavoriteJudgeRepositoryAdapter;
import com.kgu.studywithme.favorite.application.usecase.command.StudyLikeMarkingUseCase;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.favorite.infrastructure.persistence.FavoriteJpaRepository;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyLikeMarkingService implements StudyLikeMarkingUseCase {
    private final FavoriteJudgeRepositoryAdapter favoriteJudgeRepositoryAdapter;
    private final FavoriteJpaRepository favoriteJpaRepository;

    @Override
    public Long invoke(final Command command) {
        if (favoriteJudgeRepositoryAdapter.alreadyLikeMarked(command.memberId(), command.studyId())) {
            throw StudyWithMeException.type(FavoriteErrorCode.ALREADY_LIKE_MARKED);
        }

        final Favorite favoriteStudy = Favorite.favoriteMarking(command.memberId(), command.studyId());
        return favoriteJpaRepository.save(favoriteStudy).getId();
    }
}
