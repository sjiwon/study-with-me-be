package com.kgu.studywithme.favorite.application.usecase;

import com.kgu.studywithme.favorite.application.usecase.command.CancelStudyLikeCommand;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import com.kgu.studywithme.favorite.domain.service.LikeMarkingValidator;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelStudyLikeUseCase {
    private final LikeMarkingValidator likeMarkingValidator;
    private final FavoriteRepository favoriteRepository;

    public void invoke(final CancelStudyLikeCommand command) {
        if (likeMarkingValidator.neverLikeMarked(command.memberId(), command.studyId())) {
            throw StudyWithMeException.type(FavoriteErrorCode.NEVER_LIKE_MARKED);
        }

        favoriteRepository.cancelLikeMarking(command.memberId(), command.studyId());
    }
}
