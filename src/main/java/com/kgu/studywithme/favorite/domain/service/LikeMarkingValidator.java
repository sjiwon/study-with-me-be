package com.kgu.studywithme.favorite.domain.service;

import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeMarkingValidator {
    private final FavoriteRepository favoriteRepository;

    public boolean alreadyLikeMarked(final Long memberId, final Long studyId) {
        return favoriteRepository.existsByMemberIdAndStudyId(memberId, studyId);
    }

    public boolean neverLikeMarked(final Long memberId, final Long studyId) {
        return !favoriteRepository.existsByMemberIdAndStudyId(memberId, studyId);
    }
}
