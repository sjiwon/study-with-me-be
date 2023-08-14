package com.kgu.studywithme.favorite.application.service;

import com.kgu.studywithme.favorite.application.usecase.command.StudyLikeCancellationUseCase;
import com.kgu.studywithme.favorite.application.usecase.command.StudyLikeMarkingUseCase;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class FavoriteManager implements StudyLikeMarkingUseCase, StudyLikeCancellationUseCase {
    private final FavoriteRepository favoriteRepository;

    @Override
    public Long invoke(final StudyLikeMarkingUseCase.Command command) {
        validateLike(command.studyId(), command.memberId());

        final Favorite favoriteStudy = Favorite.favoriteMarking(command.studyId(), command.memberId());
        return favoriteRepository.save(favoriteStudy).getId();
    }

    private void validateLike(
            final Long studyId,
            final Long memberId
    ) {
        if (favoriteRepository.existsByStudyIdAndMemberId(studyId, memberId)) {
            throw StudyWithMeException.type(FavoriteErrorCode.ALREADY_LIKE_MARKED);
        }
    }

    @Override
    public void invoke(final StudyLikeCancellationUseCase.Command command) {
        validateCancel(command.studyId(), command.memberId());
        favoriteRepository.deleteByStudyIdAndMemberId(command.studyId(), command.memberId());
    }

    private void validateCancel(
            final Long studyId,
            final Long memberId
    ) {
        if (!favoriteRepository.existsByStudyIdAndMemberId(studyId, memberId)) {
            throw StudyWithMeException.type(FavoriteErrorCode.NEVER_LIKE_MARKED);
        }
    }
}
