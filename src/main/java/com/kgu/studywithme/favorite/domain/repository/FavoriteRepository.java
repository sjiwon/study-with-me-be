package com.kgu.studywithme.favorite.domain.repository;

import com.kgu.studywithme.favorite.domain.model.Favorite;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // @Query
    Optional<Favorite> findByStudyIdAndMemberId(final Long studyId, final Long memberId);

    default Favorite getFavoriteRecord(final Long studyId, final Long memberId) {
        return findByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> StudyWithMeException.type(FavoriteErrorCode.FAVORITE_MARKING_NOT_FOUND));
    }
}
