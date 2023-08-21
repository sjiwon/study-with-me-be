package com.kgu.studywithme.favorite.application.service;

import com.kgu.studywithme.favorite.application.adapter.FavoriteJudgeRepositoryAdapter;
import com.kgu.studywithme.favorite.application.usecase.command.StudyLikeCancellationUseCase;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.favorite.infrastructure.persistence.FavoriteJpaRepository;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyLikeCancellationService implements StudyLikeCancellationUseCase {
    private final FavoriteJudgeRepositoryAdapter favoriteJudgeRepositoryAdapter;
    private final FavoriteJpaRepository favoriteJpaRepository;

    @Override
    public void invoke(final Command command) {
        if (favoriteJudgeRepositoryAdapter.neverLikeMarked(command.memberId(), command.studyId())) {
            throw StudyWithMeException.type(FavoriteErrorCode.NEVER_LIKE_MARKED);
        }

        favoriteJpaRepository.cancelLikeMarking(command.memberId(), command.studyId());
    }
}
