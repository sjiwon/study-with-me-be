package com.kgu.studywithme.favorite.application.usecase;

import com.kgu.studywithme.favorite.application.usecase.command.MarkStudyLikeCommand;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import com.kgu.studywithme.favorite.domain.service.LikeMarkingValidator;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkStudyLikeUseCase {
    private final LikeMarkingValidator likeMarkingValidator;
    private final FavoriteRepository favoriteRepository;

    public Long invoke(final MarkStudyLikeCommand command) {
        if (likeMarkingValidator.alreadyLikeMarked(command.memberId(), command.studyId())) {
            throw StudyWithMeException.type(FavoriteErrorCode.ALREADY_LIKE_MARKED);
        }

        return favoriteRepository.save(command.toDomain()).getId();
    }
}
