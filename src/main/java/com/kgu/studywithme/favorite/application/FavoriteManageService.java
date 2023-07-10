package com.kgu.studywithme.favorite.application;

import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class FavoriteManageService {
    private final FavoriteRepository favoriteRepository;

    @StudyWithMeWritableTransactional
    public Long like(
            final Long studyId,
            final Long memberId
    ) {
        validateLike(studyId, memberId);

        final Favorite favoriteStudy = Favorite.favoriteMarking(studyId, memberId);
        return favoriteRepository.save(favoriteStudy).getId();
    }

    private void validateLike(
            final Long studyId,
            final Long memberId
    ) {
        if (favoriteRepository.existsByStudyIdAndMemberId(studyId, memberId)) {
            throw StudyWithMeException.type(FavoriteErrorCode.ALREADY_FAVORITE_MARKED);
        }
    }

    @StudyWithMeWritableTransactional
    public void cancel(
            final Long studyId,
            final Long memberId
    ) {
        validateCancel(studyId, memberId);
        favoriteRepository.deleteByStudyIdAndMemberId(studyId, memberId);
    }

    private void validateCancel(
            final Long studyId,
            final Long memberId
    ) {
        if (!favoriteRepository.existsByStudyIdAndMemberId(studyId, memberId)) {
            throw StudyWithMeException.type(FavoriteErrorCode.NOT_FAVORITE_MARKED);
        }
    }
}
