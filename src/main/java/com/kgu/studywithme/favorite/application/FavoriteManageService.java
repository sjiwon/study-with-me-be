package com.kgu.studywithme.favorite.application;

import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteManageService {
    private final FavoriteRepository favoriteRepository;

    @Transactional
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

    @Transactional
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
