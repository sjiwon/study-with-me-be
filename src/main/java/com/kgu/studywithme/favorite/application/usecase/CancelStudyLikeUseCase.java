package com.kgu.studywithme.favorite.application.usecase;

import com.kgu.studywithme.favorite.application.usecase.command.CancelStudyLikeCommand;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelStudyLikeUseCase {
    private final FavoriteRepository favoriteRepository;

    public void invoke(final CancelStudyLikeCommand command) {
        favoriteRepository.cancelLikeMarking(command.memberId(), command.studyId());
    }
}
