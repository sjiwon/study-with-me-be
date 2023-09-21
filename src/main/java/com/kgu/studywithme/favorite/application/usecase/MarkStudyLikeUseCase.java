package com.kgu.studywithme.favorite.application.usecase;

import com.kgu.studywithme.favorite.application.usecase.command.MarkStudyLikeCommand;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkStudyLikeUseCase {
    private final FavoriteRepository favoriteRepository;

    public Long invoke(final MarkStudyLikeCommand command) {
        try {
            return favoriteRepository.save(command.toDomain()).getId();
        } catch (final DataIntegrityViolationException e) {
            throw StudyWithMeException.type(FavoriteErrorCode.ALREADY_LIKE_MARKED);
        }
    }
}
