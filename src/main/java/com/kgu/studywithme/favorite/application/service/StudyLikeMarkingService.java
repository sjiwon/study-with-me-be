package com.kgu.studywithme.favorite.application.service;

import com.kgu.studywithme.favorite.application.adapter.FavoriteJudgeRepository;
import com.kgu.studywithme.favorite.application.usecase.command.StudyLikeMarkingUseCase;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyLikeMarkingService implements StudyLikeMarkingUseCase {
    private final FavoriteJudgeRepository favoriteJudgeRepository;
    private final FavoriteRepository favoriteRepository;

    @Override
    public Long invoke(final Command command) {
        if (favoriteJudgeRepository.alreadyLikeMarked(command.studyId(), command.memberId())) {
            throw StudyWithMeException.type(FavoriteErrorCode.ALREADY_LIKE_MARKED);
        }

        final Favorite favoriteStudy = Favorite.favoriteMarking(command.studyId(), command.memberId());
        return favoriteRepository.save(favoriteStudy).getId();
    }
}